package cn.lili.modules.store.serviceimpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.service.GoodsService;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.member.entity.dos.Clerk;
import cn.lili.modules.member.entity.dos.FootPrint;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dto.ClerkAddDTO;
import cn.lili.modules.member.entity.dto.CollectionDTO;
import cn.lili.modules.member.service.ClerkService;
import cn.lili.modules.member.service.FootprintService;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.store.entity.dos.Store;
import cn.lili.modules.store.entity.dos.StoreDetail;
import cn.lili.modules.store.entity.dos.StoreTenant;
import cn.lili.modules.store.entity.dto.*;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.modules.store.entity.vos.StoreSearchParams;
import cn.lili.modules.store.entity.vos.StoreVO;
import cn.lili.modules.store.mapper.StoreMapper;
import cn.lili.modules.store.service.StoreDetailService;
import cn.lili.modules.store.service.StoreService;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.StoreTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 店铺业务层实现
 *
 * @author pikachu
 * @since 2020-03-07 16:18:56
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    /**
     * 会员
     */
    @Autowired
    private MemberService memberService;

    /**
     * 店员
     */
    @Autowired
    private ClerkService clerkService;
    /**
     * 商品
     */
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * 店铺详情
     */
    @Autowired
    private StoreDetailService storeDetailService;

    @Autowired
    private StoreTenantService storeTenantService;

    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private FootprintService footprintService;

    @Autowired
    private TenantAreaService tenantAreaService;

    @Autowired
    private Cache cache;

    @Override
    public IPage<StoreVO> findByConditionPage(StoreSearchParams storeSearchParams, PageVO page) {
        IPage<StoreVO> stroePage =this.baseMapper.getStoreList(PageUtil.initPage(page), storeSearchParams.queryWrapper());
        List<StoreVO> result = new ArrayList<>();

        stroePage.getRecords().forEach(item -> {

            if (!CharSequenceUtil.isEmpty(item.getTenantId())) {
                try {
                    Tenant tenant = tenantAreaService.getById(item.getTenantId());
                    item.setTenantName(tenant.getName());
                } catch (Exception e) {
                    log.error("填充租户信息异常", e);
                }
            }
            result.add(item);
        });
        Page<StoreVO> pageResult = new Page(stroePage.getCurrent(), stroePage.getSize(),stroePage.getTotal());
        pageResult.setRecords(result);
        return pageResult;
    }

    @Override
    public StoreVO getStoreDetail() {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        // 根据当前用户ID获取店铺列表
        List<Store> currentUserStoreList = getStoreListByMemberIds(Collections.singletonList(currentUser.getId()));
        StoreVO storeVO = this.baseMapper.getStoreDetail(currentUserStoreList.get(0).getId());
        storeVO.setNickName(currentUser.getNickName());
        return storeVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Store add(AdminStoreApplyDTO adminStoreApplyDTO) {

        //判断店铺名称是否存在
        QueryWrapper<Store> queryWrapper = Wrappers.query();
        queryWrapper.eq("store_name", adminStoreApplyDTO.getStoreName());
        if (this.getOne(queryWrapper) != null) {
            throw new ServiceException(ResultCode.STORE_NAME_EXIST_ERROR);
        }

        Member member = memberService.getById(adminStoreApplyDTO.getMemberId());
        //判断用户是否存在
        if (member == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //判断是否拥有店铺
        if (Boolean.TRUE.equals(member.getHaveStore())) {
            throw new ServiceException(ResultCode.STORE_APPLY_DOUBLE_ERROR);
        }

        //添加店铺
        Store store = new Store(member, adminStoreApplyDTO);
        this.save(store);
        StoreTenant storeTenant = new StoreTenant();
        storeTenant.setStoreId(store.getId());
        storeTenant.setTenantId(adminStoreApplyDTO.getTenantId());
        storeTenant.setStoreDisable(StoreStatusEnum.APPLYING.value());
        storeTenantService.save(storeTenant);
        //判断是否存在店铺详情，如果没有则进行新建，如果存在则进行修改
        StoreDetail storeDetail = new StoreDetail(store, adminStoreApplyDTO);
        storeDetailService.save(storeDetail);
        //设置会员-店铺信息
        memberService.update(new LambdaUpdateWrapper<Member>()
                .eq(Member::getId, member.getId())
                .set(Member::getHaveStore, true)
                .set(Member::getStoreId, store.getId()));
        return store;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Store edit(StoreEditDTO storeEditDTO) {
        if (storeEditDTO != null) {
            //判断店铺名是否唯一
            Store storeTmp = getOne(new QueryWrapper<Store>().eq("store_name", storeEditDTO.getStoreName()));
            if (storeTmp != null && !CharSequenceUtil.equals(storeTmp.getId(), storeEditDTO.getStoreId())) {
                throw new ServiceException(ResultCode.STORE_NAME_EXIST_ERROR);
            }
            //修改店铺详细信息
            updateStoreDetail(storeEditDTO);
            //修改店铺信息
            return updateStore(storeEditDTO);
        } else {
            throw new ServiceException(ResultCode.STORE_NOT_EXIST);
        }
    }

    /**
     * 修改店铺基本信息
     *
     * @param storeEditDTO 修改店铺信息
     */
    private Store updateStore(StoreEditDTO storeEditDTO) {
        Store store = this.getById(storeEditDTO.getStoreId());
        if (store != null) {
            BeanUtil.copyProperties(storeEditDTO, store);
            store.setId(storeEditDTO.getStoreId());
            boolean result = this.updateById(store);
            if (result) {
                storeDetailService.updateStoreGoodsInfo(store);
            }
            String destination = rocketmqCustomProperties.getStoreTopic() + ":" + StoreTagsEnum.EDIT_STORE_SETTING.name();
            //发送订单变更mq消息
            rocketMQTemplate.asyncSend(destination, store, RocketmqSendCallbackBuilder.commonCallback());
        }

        cache.remove(CachePrefix.STORE.getPrefix() + storeEditDTO.getStoreId());
        return store;
    }

    /**
     * 修改店铺详细细腻
     *
     * @param storeEditDTO 修改店铺信息
     */
    private void updateStoreDetail(StoreEditDTO storeEditDTO) {
        StoreDetail storeDetail = new StoreDetail();
        BeanUtil.copyProperties(storeEditDTO, storeDetail);
        storeDetailService.update(storeDetail, new QueryWrapper<StoreDetail>().eq("store_id", storeEditDTO.getStoreId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(String id,String tenantID, Integer passed) {
        Store store = this.getById(id);
        if (store == null) {
            throw new ServiceException(ResultCode.STORE_NOT_EXIST);
        }
        if (passed == 0) {
            storeTenantService.ChangeStoreDisable(id,tenantID,StoreStatusEnum.OPEN.value());
            //修改会员 表示已有店铺
            Member member = memberService.getById(store.getMemberId());
            if(!member.getHaveStore()) {
                member.setHaveStore(true);
                member.setStoreId(id);
                memberService.updateById(member);
                //创建店员
                ClerkAddDTO clerkAddDTO = new ClerkAddDTO();
                clerkAddDTO.setMemberId(member.getId());
                clerkAddDTO.setIsSuper(true);
                clerkAddDTO.setShopkeeper(true);
                clerkAddDTO.setStoreId(id);
                clerkService.saveClerk(clerkAddDTO);
                //设定商家的结算日
                storeDetailService.update(new LambdaUpdateWrapper<StoreDetail>()
                        .eq(StoreDetail::getStoreId, id)
                        .set(StoreDetail::getSettlementDay, new DateTime()));
            }
        } else {
            storeTenantService.ChangeStoreDisable(id,tenantID,StoreStatusEnum.REFUSED.value());
        }

        return this.updateById(store);
    }

    @Override
    public boolean disable(String id,String tenantId) {
        Store store = this.getById(id);
        if (store != null) {

            //下架所有此店铺商品
            goodsService.underStoreGoods(id,tenantId);
            return storeTenantService.ChangeStoreDisable(id,tenantId,StoreStatusEnum.CLOSED.value());
        }

        throw new ServiceException(ResultCode.STORE_NOT_EXIST);
    }

    @Override
    public boolean enable(String id ,String tenantId) {
        Store store = this.getById(id);
        if (store != null) {
            return storeTenantService.ChangeStoreDisable(id,tenantId,StoreStatusEnum.OPEN.value());
        }
        throw new ServiceException(ResultCode.STORE_NOT_EXIST);
    }

    @Override
    public boolean applyFirstStep(StoreCompanyDTO storeCompanyDTO) {
        //获取当前操作的店铺
        Store store = getStoreByMember();

        //店铺为空，则新增店铺
        if (store == null) {
            AuthUser authUser = Objects.requireNonNull(UserContext.getCurrentUser());
            Member member = memberService.getById(authUser.getId());
            //根据会员创建店铺
            store = new Store(member);
            BeanUtil.copyProperties(storeCompanyDTO, store);
            this.save(store);
            StoreDetail storeDetail = new StoreDetail();
            storeDetail.setStoreId(store.getId());
            BeanUtil.copyProperties(storeCompanyDTO, storeDetail);
            StoreTenant storeTenant = new StoreTenant();
            storeTenant.setStoreId(store.getId());
            storeTenant.setTenantId(storeCompanyDTO.getTenantId());
            storeTenant.setStoreDisable(StoreStatusEnum.APPLY.value());
            storeTenantService.save(storeTenant);
            return storeDetailService.save(storeDetail);
        } else {
            //复制参数 修改已存在店铺
            BeanUtil.copyProperties(storeCompanyDTO, store);
            this.updateById(store);
            //判断是否存在店铺详情，如果没有则进行新建，如果存在则进行修改
            StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
            //如果店铺详情为空，则new ，否则复制对象，然后保存即可。
            if (storeDetail == null) {
                storeDetail = new StoreDetail();
                storeDetail.setStoreId(store.getId());
                BeanUtil.copyProperties(storeCompanyDTO, storeDetail);
                return storeDetailService.save(storeDetail);
            } else {
                BeanUtil.copyProperties(storeCompanyDTO, storeDetail);
                return storeDetailService.updateById(storeDetail);
            }
        }
    }

    @Override
    public boolean applySecondStep(StoreBankDTO storeBankDTO) {

        //获取当前操作的店铺
        Store store = getStoreByMember();
        StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
        //设置店铺的银行信息
        BeanUtil.copyProperties(storeBankDTO, storeDetail);
        return storeDetailService.updateById(storeDetail);
    }

    @Override
    public boolean applyThirdStep(StoreOtherInfoDTO storeOtherInfoDTO) {
        //获取当前操作的店铺
        Store store = getStoreByMember();

        BeanUtil.copyProperties(storeOtherInfoDTO, store);
        this.updateById(store);

        StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
        //设置店铺的其他信息
        BeanUtil.copyProperties(storeOtherInfoDTO, storeDetail);
        //设置店铺经营范围
        storeDetail.setGoodsManagementCategory(storeOtherInfoDTO.getGoodsManagementCategory());
        //最后一步申请，给予店铺设置库存预警默认值
        storeDetail.setStockWarning(10);
        //修改店铺详细信息
        storeDetailService.updateById(storeDetail);
        //设置店铺名称,修改店铺信息
        store.setStoreName(storeOtherInfoDTO.getStoreName());
        List<String> tenantSelectedList = (List<String>) cache.get("tenant"+store.getId());
        tenantSelectedList.forEach(tenantId->{
            storeTenantService.ChangeStoreDisable(store.getId(),tenantId,StoreStatusEnum.APPLYING.value());
        });
        cache.remove("tenant"+store.getId());
        store.setStoreCenter(storeOtherInfoDTO.getStoreCenter());
        store.setStoreDesc(storeOtherInfoDTO.getStoreDesc());
        store.setStoreLogo(storeOtherInfoDTO.getStoreLogo());
        return this.updateById(store);
    }




    @Override
    public void updateStoreGoodsNum(String storeId, Long num) {
        //修改店铺商品数量
        this.update(new LambdaUpdateWrapper<Store>()
                .set(Store::getGoodsNum, num)
                .eq(Store::getId, storeId));
    }

    @Override
    public void updateStoreCollectionNum(CollectionDTO collectionDTO) {
        baseMapper.updateCollection(collectionDTO.getId(), collectionDTO.getNum());
    }

    @Override
    public void storeToClerk() {
        //清空店铺信息方便重新导入不会有重复数据
        clerkService.remove(new LambdaQueryWrapper<Clerk>().eq(Clerk::getShopkeeper, true));
        List<Clerk> clerkList = new ArrayList<>();
        //遍历已开启的店铺
//        for (Store store : this.list(new LambdaQueryWrapper<Store>().eq(Store::getDeleteFlag, false).eq(Store::getStoreDisable, StoreStatusEnum.OPEN.name()))) {
//            clerkList.add(new Clerk(store));
//        }
        for (Store store : this.list(new LambdaQueryWrapper<Store>().eq(Store::getDeleteFlag, false))) {
            clerkList.add(new Clerk(store));
        }
        clerkService.saveBatch(clerkList);
    }

    @Override
    public List<GoodsSku> getToMemberHistory(String memberId) {
        AuthUser currentUser = UserContext.getCurrentUser();
        List<String> skuIdList = new ArrayList<>();
        for (FootPrint footPrint : footprintService.list(new LambdaUpdateWrapper<FootPrint>().eq(FootPrint::getStoreId, currentUser.getStoreId()).eq(FootPrint::getMemberId, memberId))) {
            if(footPrint.getSkuId() != null){
                skuIdList.add(footPrint.getSkuId());
            }
        }
        return goodsSkuService.getGoodsSkuByIdFromCache(skuIdList);
    }

    @Override
    public String getStore(Member member) {
        Store store = new Store(member);
        store.setStoreName(member.getNickName());
        store.setSelfOperated(true);
        this.save(store);
        return store.getId();
    }

    @Override
    public List<Store> getStoreListByMemberIds(List<String> ids) {
        return this.list(new LambdaQueryWrapper<Store>().in(Store::getMemberId, ids));
    }

    /**
     * 获取当前登录操作的店铺
     *
     * @return 店铺信息
     */
    private Store getStoreByMember() {
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (UserContext.getCurrentUser() != null) {
            lambdaQueryWrapper.eq(Store::getMemberId, UserContext.getCurrentUser().getId());
        }
        return this.getOne(lambdaQueryWrapper, false);
    }

}
