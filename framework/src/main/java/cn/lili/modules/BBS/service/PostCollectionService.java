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

import cn.lili.modules.BBS.entity.PostCollectionEntity;
import cn.lili.modules.BBS.param.AddCollectionForm;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-24 20:49:32
 */
public interface PostCollectionService extends IService<PostCollectionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer collectCount(Integer postId);

    Boolean isCollection(String uid,Integer postId);

    void cancelCollection(AddCollectionForm request,String uid);

    void cancelALLCollection(Integer pid);

    List<Integer> getPostListByUid(String uid);
}

