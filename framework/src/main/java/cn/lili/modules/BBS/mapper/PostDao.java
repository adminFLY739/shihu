/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 商业版演示站点: https://www.linfeng.tech
 * 商业版购买联系技术客服
 * QQ:  3582996245
 * 可正常分享和学习源码，不得转卖或非法牟利！
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.mapper;

import cn.lili.modules.BBS.entity.PostEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-23 20:49:55
 */
@Mapper
public interface PostDao extends BaseMapper<PostEntity> {

    @Select("select * from lf_post WHERE discuss_id=#{id}")
    List<PostEntity> getPostListByDiscussId(Integer id);

}
