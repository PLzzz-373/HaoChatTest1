package com.gugugu.haochat.user.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberExtraResp;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMemberCursorReq;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.enums.YesOrNoEnum;
import com.gugugu.haochat.common.domain.enums.user.ItemTypeEnum;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.CursorUtil;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDAO extends ServiceImpl<UserMapper, User> {
    @Resource
    private UserBackpackDAO userBackpackDAO;
    @Resource
    private UserCache userCache;
    public User getByOpenId(String openid) {
        return lambdaQuery()
                .eq(User::getOpenId, openid)
                .one();
    }

    public User getByUsername(String username) {
        return lambdaQuery()
                .eq(User::getName, username)
                .one();
    }

    public Boolean updateUsername(Long uid, String username) {
        //使用改名卡
        userBackpackDAO.updateStatus(uid, ItemTypeEnum.MODIFY_NAME_CARD.getType(), YesOrNoEnum.YES.getStatus());
        //改名
        User user = new User();
        user.setId(uid);
        user.setName(username);
        //更新缓存
        userCache.updateUserInfo(user);
        return this.updateById(user);
    }

    public CursorPageBaseResp<User, String> getCursorPage(List<Long> uidList, ChatMemberCursorReq req) {
        return CursorUtil.getCursorPageByMysql(this,req, wrapper ->{
            wrapper.eq(User::getActiveStatus,req.getActiveStatus());
            wrapper.in(CollectionUtil.isNotEmpty(uidList), User::getId, uidList);
        }, User::getUpdateTime);
    }

    public Page<User> getBachByNameWithAmbiguous(PageReq<String> pageReq) {
        Page<User> userPage = new Page<>(pageReq.getCurrent(), pageReq.getPageSize(), true);
        return this.lambdaQuery()
                .like(StrUtil.isNotEmpty(pageReq.getData()),User::getName,pageReq.getData())
                .select(User::getId)
                .page(userPage);
    }

    public List<User> getBath(List<Long> uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            return this.lambdaQuery()
                    .in(User::getId, uidList)
                    .list();
        } else {
            return new ArrayList<>();
        }
    }
}
