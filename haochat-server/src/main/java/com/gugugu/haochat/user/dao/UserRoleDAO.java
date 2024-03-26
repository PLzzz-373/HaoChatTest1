package com.gugugu.haochat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.entity.UserRole;
import com.gugugu.haochat.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class UserRoleDAO extends ServiceImpl<UserRoleMapper, UserRole> {

    public Long getPower(User user) {
        LambdaQueryWrapper<UserRole> wrapper = new QueryWrapper<UserRole>()
                .lambda().eq(UserRole::getUid, user.getId());
        UserRole userRole = this.getOne(wrapper);
        if (userRole == null) {
            return 0L;
        }
        Long roleId = userRole.getRoleId();
        AssertUtil.isNotEmpty(roleId, CommonErrorEnum.SYSTEM_ERROR.getMsg());
        return userRole.getRoleId();
    }
}
