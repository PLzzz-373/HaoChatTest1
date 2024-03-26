package com.gugugu.haochat.wechat.adapter;

import cn.hutool.core.util.RandomUtil;
import com.gugugu.haochat.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

public class UserAdapter {

    /**
     * 构建已授权的用户对象
     *
     * @param uid 用户ID
     * @param userInfo 用户信息
     * @return 用户信息
     */
    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }
}
