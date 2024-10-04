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
package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-24 21:29:22
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer getCountByTopicId(Integer id);

//    void deleteByAdmin(Long id);


    void deleteByPid(Integer id);

    void deleteById(Long id);

    Integer getCountByPostId(Integer id);

    AppPageUtils queryCommentPage(Integer postId, Integer page);

//    Integer getYesterdayCount();
//
//    Integer getAllCount();
}

