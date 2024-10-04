package cn.lili.modules.search.service;

import cn.lili.modules.search.entity.dos.HotWordsHistory;
import cn.lili.modules.search.entity.dto.HotWordsDTO;

import java.util.List;

/**
 * HotWordsService
 *
 * @author Chopper
 * @version v1.0
 * 2022-04-14 09:35
 */
public interface HotWordsService {

    /**
     * 获取热门关键词
     *
     * @param count 热词数量
     * @param tenantId 租户id
     * @return 热词集合
     */
    List<String> getHotWords(Integer count,String tenantId);

    /**
     * 获取热门关键词
     *
     * @param count 热词数量
     * @param tenantId  租户id
     * @return 热词集合
     */
    List<HotWordsHistory> getHotWordsVO(Integer count,String tenantId);

    /**
     * 设置热门关键词
     *
     * @param hotWords 热词分数
     */
    void setHotWords(HotWordsDTO hotWords);

    /**
     * 删除热门关键词
     *
     * @param keywords 热词
     * @param tenantId 租户id
     */
    void deleteHotWords(String keywords,String tenantId);

}
