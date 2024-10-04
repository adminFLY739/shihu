/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 商业版演示站点: https://www.linfeng.tech
 * 商业版购买联系技术客服
 * QQ:  3582996245
 * 可正常分享和学习源码，不得专卖或非法牟利！
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.service.impl;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.LinfengException;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.BBS.entity.CategoryEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.mapper.CategoryDao;
import cn.lili.modules.BBS.service.CategoryService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.utils.PageUtils;
import cn.lili.modules.BBS.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private PostService postService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 分类保存
     *
     * @param category
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCategory(CategoryEntity category) {
        Integer count = this.lambdaQuery().eq(CategoryEntity::getCateName, category.getCateName()).count().intValue();
        if (count != 0) {
            throw new LinfengException("分类名不能重复");
        }
        this.save(category);
    }

    /**
     * 删除分类
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdList(List<Integer> list) {
        list.forEach(id -> {
            Integer count = postService.lambdaQuery().eq(PostEntity::getCut, id).count().intValue();
            if (count > 0) {
                CategoryEntity category = this.getById(id);
                throw new LinfengException(category.getCateName() + "分类下存在帖子未删除");
            }
        });
        this.removeByIds(list);
    }

    @Override
    public boolean deletePostCategory(List<Integer> ids) {
        boolean result = false;
        for (Integer id : ids) {
            //如果此规格绑定分类则不允许删除
            //查询某商品分类的商品数量
            long count = postService.getPostNumByCut(id);
            if (count > 0) {
                throw new ServiceException(ResultCode.CATEGORY_HAS_POST);
            }
            //删除规格
            result = this.removeById(id);
        }
        return result;
    }

}
