package cn.lili.modules.page.serviceimpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.BeanUtil;
import cn.lili.modules.page.entity.dos.Article;
import cn.lili.modules.page.entity.dto.ArticleSearchParams;
import cn.lili.modules.page.entity.enums.ArticleEnum;
import cn.lili.modules.page.entity.vos.ArticleVO;
import cn.lili.modules.page.mapper.ArticleMapper;
import cn.lili.modules.page.service.ArticleService;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 文章业务层实现
 *
 * @author Chopper
 * @since 2020/11/18 11:40 上午
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private TenantAreaService tenantAreaService;

    @Autowired
    private StoreTenantService storeTenantService;

    @Override
    public IPage<ArticleVO> managerArticlePage(ArticleSearchParams articleSearchParams) {
        articleSearchParams.setSort("a.sort");
        IPage<ArticleVO> articleVOIPage = this.baseMapper.getArticleList(PageUtil.initPage(articleSearchParams), articleSearchParams.queryWrapper());
        List<ArticleVO> articleVOS = articleVOIPage.getRecords();
        articleVOS.forEach(item->{
            if(!item.getTenantId().equals("0")) {
                item.setName(tenantAreaService.getById(item.getTenantId()).getName());
            }
            else{
                item.setName("管理员");
            }
        });
        articleVOIPage.setRecords(articleVOS);
        return articleVOIPage;
    }

    @Override
    public IPage<ArticleVO> articlePage(ArticleSearchParams articleSearchParams) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        List<Tenant> tenantList = storeTenantService.getTenantListByStoreId(storeId);
        articleSearchParams.setTenantId(tenantList.get(0).getId());
        articleSearchParams.setSort("a.sort");
        QueryWrapper queryWrapper = articleSearchParams.queryWrapper();
        queryWrapper.eq("open_status", true);
        return this.baseMapper.getArticleList(PageUtil.initPage(articleSearchParams), queryWrapper);
    }

    @Override
    public List<Article> list(String categoryId) {

        QueryWrapper<Article> queryWrapper = Wrappers.query();
        queryWrapper.eq(StringUtils.isNotBlank(categoryId), "category_id", categoryId);
        return this.list(queryWrapper);
    }


    @Override
    public Article updateArticle(Article article) {
        Article oldArticle = this.getById(article.getId());
        BeanUtil.copyProperties(article, oldArticle);
        this.updateById(oldArticle);
        return oldArticle;
    }

    @Override
    public void customRemove(String id) {
        //判断是否为默认文章
        if (this.getById(id).getType().equals(ArticleEnum.OTHER.name())) {
            this.removeById(id);
        } else {
            throw new ServiceException(ResultCode.ARTICLE_NO_DELETION);
        }
    }

    @Override
    public Article customGet(String id) {
        return this.getById(id);
    }

    @Override
    public Article customGetByType(String type,String tenantId) {
        if (!CharSequenceUtil.equals(type, ArticleEnum.OTHER.name())) {
            return this.getOne(new LambdaUpdateWrapper<Article>().eq(Article::getType, type).eq(Article::getTenantId,tenantId));
        }
        return null;
    }

    @Override
    public Boolean updateArticleStatus(String id, boolean status) {
        Article article = this.getById(id);
        article.setOpenStatus(status);
        return this.updateById(article);
    }

    @Override
    public Article updateArticleType(Article article) {
        Article oldArticle = this.getById(article.getId());
        BeanUtil.copyProperties(article, oldArticle);
        this.updateById(oldArticle);
        return oldArticle;
    }

  @Override
  public List<Article> getByTenantId(String tenantId) {
      QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("tenant_id",tenantId);
      return this.list(queryWrapper);
  }
}
