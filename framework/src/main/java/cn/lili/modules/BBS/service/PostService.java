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


import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.entity.vo.PostDetailResponse;
import cn.lili.modules.BBS.param.*;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-23 20:49:55
 */
public interface PostService extends IService<PostEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteByAdmin(Integer id);

    Integer getPostNumByUid(String uid);

    Long getPostNumByCut(Integer cut);

    AppPageUtils lastPost(Integer currPage,Integer classId);

    AppPageUtils followUserPost(Integer page, String userId);

    void addCollection(AddCollectionForm request, String userId);

    AppPageUtils myPost(Integer page, String uid);

    AppPageUtils myCollectPost(Integer page,String uid);

    PostDetailResponse detail(Integer id);

    void addComment(AddCommentForm request, String uid);


    void delComment(DelCommentForm request, String uid);

    Integer addPost(AddPostForm request, String uid);

    AppPageUtils queryPageList(PostListForm request, String uid);


    void delPost(AddCollectionForm request,String uid);

    Integer updatePost(AddPostForm request, String uid);

    Integer getPostCountByDiscussId(Integer id);

    Integer addManagerPost(AddManagerPostForm request);
}

