package cn.lili.modules.goods.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.goods.entity.dos.Category;
import cn.lili.modules.goods.entity.vos.CategoryVO;
import cn.lili.modules.goods.mapper.CategoryMapper;
import cn.lili.modules.goods.service.CategoryBrandService;
import cn.lili.modules.goods.service.CategoryParameterGroupService;
import cn.lili.modules.goods.service.CategoryService;
import cn.lili.modules.goods.service.CategorySpecificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品分类业务层实现
 *
 * @author pikachu
 * @since 2020-02-23 15:18:56
 */
@Service
@CacheConfig(cacheNames = "{CATEGORY}")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private static final String DELETE_FLAG_COLUMN = "delete_flag";
    /**
     * 缓存
     */
    @Autowired
    private Cache cache;

    /**
     * 分类品牌绑定
     */
    @Autowired
    private CategoryBrandService categoryBrandService;
    /**
     * 分类参数绑定
     */
    @Autowired
    private CategoryParameterGroupService categoryParameterGroupService;

    /**
     * 分类规格绑定
     */
    @Autowired
    private CategorySpecificationService categorySpecificationService;

    @Override
    public List<Category> dbList(String parentId) {
        return this.list(new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId));
    }

    @Override
    @Cacheable(key = "#id")
    public Category getCategoryById(String id) {
        return this.getById(id);
    }

    /**
     * 根据分类id集合获取所有分类根据层级排序
     *
     * @param ids 分类ID集合
     * @return 商品分类列表
     */
    @Override
    public List<Category> listByIdsOrderByLevel(List<String> ids) {
        return this.list(new LambdaQueryWrapper<Category>().in(Category::getId, ids).orderByAsc(Category::getLevel));
    }

    @Override
    public List<Map<String, Object>> listMapsByIdsOrderByLevel(List<String> ids, String columns) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(columns);
        queryWrapper.in("id", ids).orderByAsc("level");
        return this.listMaps(queryWrapper);
    }

    @Override
    public List<CategoryVO> categoryTree(String tenantId) {
        // 从缓存中获取分类列表
        List<CategoryVO> categoryVOList = (List<CategoryVO>) cache.get(CachePrefix.CATEGORY.getPrefix()+tenantId);
        // 如果分类列表不为空则直接返回，因为同一个租户的分类列表是不经常变化的，可以使用缓存储存
        if (categoryVOList != null) {
            return categoryVOList;
        }

        // 1.获取全部分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 根据tenantId查询分类，即根据租户ID查询分类信息
        queryWrapper.eq(Category::getTenantId,tenantId);
        // 查询未被删除的分类
        queryWrapper.eq(Category::getDeleteFlag, false);
        // 执行查询操作，获取分类列表
        List<Category> list = this.list(queryWrapper);

        // 2.构造分类树
        // 创建一个空的分类VO列表
        categoryVOList = new ArrayList<>();
        // 遍历分类列表
        for (Category category : list) {
            // 如果分类的父ID为"0"，表示是顶级分类
            if ("0".equals(category.getParentId())) {
                // 创建一个CategoryVO对象，用于表示当前分类
                CategoryVO categoryVO = new CategoryVO(category);
                // 递归查找当前分类的子分类，并设置为其子节点
                categoryVO.setChildren(findChildren(list, categoryVO));
                // 将当前分类添加到分类VO列表中
                categoryVOList.add(categoryVO);
            }
        }
        // 根据排序顺序对分类VO列表进行排序
        categoryVOList.sort(Comparator.comparing(Category::getSortOrder));
        // 如果分类VO列表非空
        if (!categoryVOList.isEmpty()) {
            // 将分类VO列表放入缓存，以tenantId作为缓存的键
            cache.put(CachePrefix.CATEGORY.getPrefix()+tenantId, categoryVOList);
            // 将原始的分类列表也放入缓存，以tenantId作为缓存的键
            cache.put(CachePrefix.CATEGORY_ARRAY.getPrefix()+tenantId, list);
        }
        return categoryVOList;    // 返回构建好的分类树
    }


    @Override
    public List<CategoryVO> getStoreCategory(String[] categories,String tenantId) {
        List<String> arr = Arrays.asList(categories.clone());
        return categoryTree(tenantId).stream()
                .filter(item -> arr.contains(item.getId())).collect(Collectors.toList());
    }

    @Override
    public List<Category> firstCategory() {
        QueryWrapper<Category> queryWrapper = Wrappers.query();
        queryWrapper.eq("level", 0);
        return list(queryWrapper);
    }

    @Override
    public List<CategoryVO> listAllChildren(String parentId,String tenantId) {
        if ("0".equals(parentId)) {
            return categoryTree(tenantId);
        }
        //循环代码，找到对象，把他的子分类返回
        List<CategoryVO> topCatList = categoryTree(tenantId);
        for (CategoryVO item : topCatList) {
            if (item.getId().equals(parentId)) {
                return item.getChildren();
            } else {
                return getChildren(parentId, item.getChildren());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<CategoryVO> listAllChildrens(String tenantId) {

       LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleteFlag, false);
        queryWrapper.eq(Category::getTenantId ,tenantId);
        //获取全部分类
        List<Category> list = this.list(queryWrapper);

        //构造分类树
        List<CategoryVO> categoryVOList = new ArrayList<>();
        for (Category category : list) {
            if (("0").equals(category.getParentId())) {
                CategoryVO categoryVO = new CategoryVO(category);
                categoryVO.setChildren(findChildren(list, categoryVO));
                categoryVOList.add(categoryVO);
            }
        }
        categoryVOList.sort(Comparator.comparing(Category::getSortOrder));
        return categoryVOList;
    }

    /**
     * 获取指定分类的分类名称
     *
     * @param ids 指定分类id集合
     * @return 分类名称集合
     */
    @Override
    public List<String> getCategoryNameByIds(List<String> ids,String tenantId) {
        List<String> categoryName = new ArrayList<>();
        List<Category> categoryVOList = (List<Category>) cache.get(CachePrefix.CATEGORY_ARRAY.getPrefix()+tenantId);
        //如果缓存中为空，则重新获取缓存
        if (categoryVOList == null) {
            categoryTree(tenantId);
            categoryVOList = (List<Category>) cache.get(CachePrefix.CATEGORY_ARRAY.getPrefix()+tenantId);
        }
        //还为空的话，直接返回
        if (categoryVOList == null) {
            return new ArrayList<>();
        }
        //循环顶级分类
        for (Category category : categoryVOList) {
            //循环查询的id匹配
            for (String id : ids) {
                if (category.getId().equals(id)) {
                    //写入商品分类
                    categoryName.add(category.getName());
                }
            }
        }
        return categoryName;
    }

    @Override
    public List<Category> findByAllBySortOrder(Category category) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(category.getLevel() != null, "level", category.getLevel())
                .eq(CharSequenceUtil.isNotBlank(category.getName()), "name", category.getName())
                .eq(category.getParentId() != null, "parent_id", category.getParentId())
                .ne(category.getId() != null, "id", category.getId())
                .eq(DELETE_FLAG_COLUMN, false)
                .orderByAsc("sort_order");
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCategory(Category category) {
        //判断分类佣金是否正确
        if (category.getCommissionRate() < 0) {
            throw new ServiceException(ResultCode.CATEGORY_COMMISSION_RATE_ERROR);
        }
        //子分类与父分类的状态一致
        if (category.getParentId() != null && !("0").equals(category.getParentId())) {
            Category parentCategory = this.getById(category.getParentId());
            category.setDeleteFlag(parentCategory.getDeleteFlag());
        }
        this.save(category);
        removeCache(category.getTenantId());
        return true;
    }

    @Override
    @CacheEvict(key = "#category.id")
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Category category) {
        //判断分类佣金是否正确
        if (category.getCommissionRate() < 0) {
            throw new ServiceException(ResultCode.CATEGORY_COMMISSION_RATE_ERROR);
        }
        //判断父分类与子分类的状态是否一致
        if (category.getParentId() != null && !"0".equals(category.getParentId())) {
            Category parentCategory = this.getById(category.getParentId());
            if (!parentCategory.getDeleteFlag().equals(category.getDeleteFlag())) {
                throw new ServiceException(ResultCode.CATEGORY_DELETE_FLAG_ERROR);
            }
        }
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", category.getId());
        this.baseMapper.update(category, updateWrapper);
        removeCache(category.getTenantId());
    }


    @Override
    @CacheEvict(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        Category category = this.getById(id);
        this.removeById(id);
        removeCache(category.getTenantId());
        //删除关联关系
        categoryBrandService.deleteByCategoryId(id);
        categoryParameterGroupService.deleteByCategoryId(id);
        categorySpecificationService.deleteByCategoryId(id);
    }

    @Override
    @CacheEvict(key = "#categoryId")
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryStatus(String categoryId, Boolean enableOperations) {
        //禁用子分类
        CategoryVO categoryVO = new CategoryVO(this.getById(categoryId));
        List<String> ids = new ArrayList<>();
        ids.add(categoryVO.getId());
        this.findAllChild(categoryVO);
        this.findAllChildIds(categoryVO, ids);
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Category::getId, ids);
        updateWrapper.set(Category::getDeleteFlag, enableOperations);
        this.update(updateWrapper);
        removeCache(categoryVO.getTenantId());
    }

    /**
     * 递归树形VO
     *
     * @param categories 分类列表
     * @param categoryVO 分类VO
     * @return 分类VO列表
     */
    private List<CategoryVO> findChildren(List<Category> categories, CategoryVO categoryVO) {
        // 创建一个空的子分类VO列表
        List<CategoryVO> children = new ArrayList<>();

        // 遍历分类列表
        categories.forEach(item -> {
            // 如果当前分类的父ID与指定分类VO的ID相等，表示是指定分类VO的子分类
            if (item.getParentId().equals(categoryVO.getId())) {
                // 创建一个CategoryVO对象，用于表示当前子分类
                CategoryVO temp = new CategoryVO(item);
                // 递归查找当前子分类的子分类，并设置为其子节点
                temp.setChildren(findChildren(categories, temp));
                // 将当前子分类添加到子分类VO列表中
                children.add(temp);
            }
        });

        // 返回子分类VO列表
        return children;
    }


    /**
     * 条件查询分类
     *
     * @param category 分类VO
     */
    private void findAllChild(CategoryVO category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, category.getId());
        List<Category> categories = this.list(queryWrapper);
        List<CategoryVO> categoryVOList = new ArrayList<>();
        for (Category category1 : categories) {
            categoryVOList.add(new CategoryVO(category1));
        }
        category.setChildren(categoryVOList);
        if (!categoryVOList.isEmpty()) {
            categoryVOList.forEach(this::findAllChild);
        }
    }

    /**
     * 获取所有的子分类ID
     *
     * @param category 分类
     * @param ids      ID列表
     */
    private void findAllChildIds(CategoryVO category, List<String> ids) {
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            for (CategoryVO child : category.getChildren()) {
                ids.add(child.getId());
                this.findAllChildIds(child, ids);
            }
        }
    }

    /**
     * 递归自身，找到id等于parentId的对象，获取他的children 返回
     *
     * @param parentId       父ID
     * @param categoryVOList 分类VO
     * @return 子分类列表VO
     */
    private List<CategoryVO> getChildren(String parentId, List<CategoryVO> categoryVOList) {
        for (CategoryVO item : categoryVOList) {
            if (item.getId().equals(parentId)) {
                return item.getChildren();
            }
            if (item.getChildren() != null && !item.getChildren().isEmpty()) {
                return getChildren(parentId, item.getChildren());
            }
        }
        return categoryVOList;
    }

    /**
     * 清除缓存
     */
    private void removeCache(String tenantId) {
        cache.remove(CachePrefix.CATEGORY.getPrefix()+tenantId);
        cache.remove(CachePrefix.CATEGORY_ARRAY.getPrefix()+tenantId);
    }

    @Override
    public List<Category> getItem(String id) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getParentId, id);
        List<Category> categories = this.list(lambdaQueryWrapper);
        categories.sort(Comparator.comparing(Category::getSortOrder));
        return categories;
    }
}
