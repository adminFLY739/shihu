package cn.lili.modules.member.serviceimpl;

import cn.lili.common.security.context.UserContext;;
import cn.lili.modules.member.entity.dos.FootPrint;
import cn.lili.modules.member.entity.dto.FootPrintQueryParams;
import cn.lili.modules.member.mapper.FootprintMapper;
import cn.lili.modules.member.service.FootprintService;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import cn.lili.modules.search.service.EsGoodsSearchService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会员浏览历史业务层实现
 *
 * @author Chopper
 * @since 2020/11/18 10:46 上午
 */
@Service
public class FootprintServiceImpl extends ServiceImpl<FootprintMapper, FootPrint> implements FootprintService {

    /**
     * es商品
     */
    @Autowired
    private EsGoodsSearchService esGoodsSearchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FootPrint saveFootprint(FootPrint footPrint) {
        LambdaQueryWrapper<FootPrint> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FootPrint::getMemberId, footPrint.getMemberId());
        queryWrapper.eq(FootPrint::getGoodsId, footPrint.getGoodsId());
        queryWrapper.eq(FootPrint::getTenantId, footPrint.getTenantId());
        //如果已存在某商品记录，则更新其修改时间
        //如果不存在则添加记录
        //为了保证足迹的排序,将原本足迹删除后重新添加
        List<FootPrint> oldPrints = list(queryWrapper);
        if (oldPrints != null && !oldPrints.isEmpty()) {
            FootPrint oldPrint = oldPrints.get(0);
            this.removeById(oldPrint.getId());
        }
        footPrint.setCreateTime(new Date());
        this.save(footPrint);
        //删除超过100条后的记录
        this.baseMapper.deleteLastFootPrint(footPrint.getMemberId());
        return footPrint;
    }

    @Override
    public boolean clean() {
        LambdaQueryWrapper<FootPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FootPrint::getMemberId, Objects.requireNonNull(UserContext.getCurrentUser()).getId());
        return this.remove(lambdaQueryWrapper);
    }

    @Override
    public boolean deleteByIds(List<String> ids) {
        LambdaQueryWrapper<FootPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FootPrint::getMemberId, Objects.requireNonNull(UserContext.getCurrentUser()).getId());
        lambdaQueryWrapper.in(FootPrint::getGoodsId, ids);
        return this.remove(lambdaQueryWrapper);
    }

    @Override
    public IPage<EsGoodsIndex> footPrintPage(FootPrintQueryParams params) {
        IPage<FootPrint> footPrintPages = this.page(PageUtil.initPage(params), params.queryWrapper());
        //定义结果
        IPage<EsGoodsIndex> esGoodsIndexIPage = new Page<>();

        if (footPrintPages.getRecords() == null || footPrintPages.getRecords().isEmpty()) {
            return esGoodsIndexIPage;
        } else {
            List<EsGoodsIndex> list = esGoodsSearchService.getEsGoodsBySkuIds(
                    footPrintPages.getRecords().stream().map(FootPrint::getSkuId).collect(Collectors.toList()), params);
            esGoodsIndexIPage.setPages(footPrintPages.getPages());
            esGoodsIndexIPage.setRecords(list);
            esGoodsIndexIPage.setTotal(footPrintPages.getTotal());
            esGoodsIndexIPage.setSize(footPrintPages.getSize());
            esGoodsIndexIPage.setCurrent(footPrintPages.getCurrent());
            return esGoodsIndexIPage;
        }
    }

    @Override
    public long getFootprintNum(String tenantId) {
        LambdaQueryWrapper<FootPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FootPrint::getMemberId, Objects.requireNonNull(UserContext.getCurrentUser()).getId());
        lambdaQueryWrapper.eq(FootPrint::getTenantId, tenantId);
        lambdaQueryWrapper.eq(FootPrint::getDeleteFlag, false);
        return this.count(lambdaQueryWrapper);
    }
}
