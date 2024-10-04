package cn.lili.modules.system.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.mapper.SettingMapper;
import cn.lili.modules.system.service.SettingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 配置业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 3:52 下午
 */
@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {

    @Override
    public Setting get(String key) {
        return this.getById(key);
    }

    @Override
    public boolean saveUpdate(Setting setting) {
      LambdaUpdateWrapper<Setting> updateWrapper = new LambdaUpdateWrapper<>();
      updateWrapper.eq(CharSequenceUtil.isNotEmpty(setting.getId()), Setting::getId, setting.getId());
      updateWrapper.eq(CharSequenceUtil.isNotEmpty(setting.getTenantId()), Setting::getTenantId, setting.getTenantId());
      updateWrapper.set(Setting::getSettingValue, setting.getSettingValue());
      return this.update(updateWrapper);
    }

  @Override
  public Setting getByIdAndTenantId(String name, String tenantId) {
    QueryWrapper<Setting> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id",name);
    queryWrapper.eq("tenant_id",tenantId);
    return  this.getOne(queryWrapper);
  }
}
