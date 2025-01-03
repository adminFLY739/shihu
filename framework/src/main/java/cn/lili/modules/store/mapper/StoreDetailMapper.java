package cn.lili.modules.store.mapper;

import cn.hutool.core.date.DateTime;
import cn.lili.modules.store.entity.dos.StoreDetail;
import cn.lili.modules.store.entity.dto.StoreAfterSaleAddressDTO;
import cn.lili.modules.store.entity.dto.StoreDeliverGoodsAddressDTO;
import cn.lili.modules.store.entity.dto.StoreSettlementDay;
import cn.lili.modules.store.entity.vos.StoreBasicInfoVO;
import cn.lili.modules.store.entity.vos.StoreDetailVO;
import cn.lili.modules.store.entity.vos.StoreOtherVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 店铺详细数据处理层
 *
 * @author pikachu
 * @since 2020-03-07 09:18:56
 */
public interface StoreDetailMapper extends BaseMapper<StoreDetail> {

    /**
     * 获取店铺详情VO
     *
     * @param storeId 店铺ID
     * @param tenantId 租户ID
     * @return 店铺详情VO
     */
    @Select("select s.store_logo,s.member_name,s.store_name,st.store_disable,s.self_operated,s.store_address_detail,s.store_address_path,s.store_address_id_path,s.store_center,s.store_desc,s.yzf_sign,s.yzf_mp_sign," +
            "d.* from li_store_tenant st join  li_store s on st.store_id = s.id inner join li_store_detail d on s.id=d.store_id where s.id=#{storeId} and st.tenant_id = #{tenantId}")
    StoreDetailVO getStoreDetail(String storeId,String tenantId);

    /**
     * 根据会员ID获取店铺详情
     *
     * @param memberId 会员ID
     * @return 店铺详情
     */
    @Select("select s.member_name,s.store_name,s.self_operated,s.store_center,st.store_disable,s.store_logo,s.store_desc,s.store_address_detail,s.store_address_path,s.store_address_id_path,d.* " +
            "from  li_store_tenant st join  li_store s on st.store_id = s.id inner join li_store_detail d on s.id=d.store_id where s.member_id=#{memberId} and st.tenant_id = #{tenantId}")
    StoreDetailVO getStoreDetailByMemberId(String memberId,String tenantId);

    /**
     * 获取店铺基础信息DTO
     *
     * @param storeId 店铺ID
     * @return 店铺基础信息DTO
     */
    @Select("SELECT s.id as storeId,s.* FROM li_store s WHERE s.id=#{storeId}")
    StoreBasicInfoVO getStoreBasicInfoDTO(String storeId);

    /**
     * 获取店铺售后地址DTO
     *
     * @param storeId 店铺ID
     * @return 店铺售后地址DTO
     */
    @Select("select s.sales_consignee_name,s.sales_consignee_mobile,s.sales_consignee_address_id,s.sales_consignee_address_path,s.sales_consignee_detail " +
            "from li_store_detail s  where s.store_id=#{storeId}")
    StoreAfterSaleAddressDTO getStoreAfterSaleAddressDTO(String storeId);

    /**
     * 获取待结算店铺列表
     *
     * @param day 结算日
     * @return 待结算店铺列表
     */
    @Select("SELECT store_id,settlement_day FROM li_store_detail " +
            "WHERE settlement_cycle LIKE concat(#{day},',%')  " +
            "OR settlement_cycle LIKE concat('%,',#{day},',%') " +
            "OR settlement_cycle LIKE concat('%,',#{day})"+
            "OR settlement_cycle = #{day}")
    List<StoreSettlementDay> getSettlementStore(int day);

    /**
     * 修改店铺的结算日
     *
     * @param storeId  店铺ID
     * @param dateTime 结算日
     */
    @Update("UPDATE li_store_detail SET settlement_day=#{dateTime} WHERE store_id=#{storeId}")
    void updateSettlementDay(String storeId, DateTime dateTime);

    /**
     * 查看店铺营业执照信息
     * @param storeId 店铺ID
     * @return 店铺营业执照
     */
    @Select("SELECT * FROM li_store_detail WHERE store_id=#{storeId}")
    StoreOtherVO getLicencePhoto(String storeId);

    /***
     * 获取店铺发货地址
     * @param storeId 店铺ID
     * @return 店铺发货地址DTO
     */
    @Select("select s.sales_consignor_name,s.sales_consignor_mobile,s.sales_consignor_address_id,s.sales_consignor_address_path,s.sales_consignor_detail "+
            "from li_store_detail s  where s.store_id=#{storeId}")
    StoreDeliverGoodsAddressDTO getStoreDeliverGoodsAddressDto(String storeId);
}
