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
package cn.lili.modules.BBS.service.impl;

import cn.lili.modules.BBS.entity.SystemEntity;
import cn.lili.modules.BBS.mapper.SystemDao;
import cn.lili.modules.BBS.service.SystemService;
import cn.lili.modules.BBS.utils.PageUtils;
import cn.lili.modules.BBS.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("systemService")
public class SystemServiceImpl extends ServiceImpl<SystemDao, SystemEntity> implements SystemService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystemEntity> page = this.page(
                new Query<SystemEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


}
