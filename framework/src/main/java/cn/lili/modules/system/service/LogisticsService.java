package cn.lili.modules.system.service;

import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.vos.GoodsVO;
import cn.lili.modules.system.entity.dos.Logistics;
import cn.lili.modules.system.entity.dto.LogisticsSearchParams;
import cn.lili.modules.system.entity.vo.Traces;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 物流公司业务层
 *
 * @author Chopper
 * @since 2020/11/17 8:02 下午
 */
public interface LogisticsService extends IService<Logistics> {

    /**
     * 查询物流信息
     *
     * @param logisticsId 物流公司ID
     * @param logisticsNo 单号
     * @param phone       手机号
     * @return 物流信息
     */
    Traces getLogisticTrack(String logisticsId, String logisticsNo, String phone,String tenantId);


    Traces getLogisticMapTrack(String logisticsId, String logisticsNo, String phone, String from, String to,String tenantId);

    String labelOrder(String orderSn, String logisticsId);

    /**
     * 获取已开启的物流公司列表
     *
     * @param tenantId 租户id
     * @return 物流公司列表
     */
    List<Logistics> getOpenLogistics(String tenantId);

    /**
     * 物流公司查询
     *
     * @param logisticsSearchParams 查询参数
     * @return 商品分页
     */
    IPage<Logistics> queryByParams(LogisticsSearchParams logisticsSearchParams);
}
