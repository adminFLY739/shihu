/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服 QQ: 3582996245
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 演示站点:https://www.linfeng.tech
 * 版权所有，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.entity.CommentThumbsEntity;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-25 19:00:24
 */
public interface CommentThumbsService extends IService<CommentThumbsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean isThumbs(String uid, Long id);

    Integer getThumbsCount(Long id);

    void addThumbs(AddThumbsForm request, String uid);

    void cancelThumbs(AddThumbsForm request, String uid);

    void cancelAllThumbs(List<CommentEntity> cList);
}

