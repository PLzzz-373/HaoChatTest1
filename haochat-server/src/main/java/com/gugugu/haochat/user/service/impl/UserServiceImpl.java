package com.gugugu.haochat.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gugugu.haochat.chat.dao.RoomFriendDAO;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.UserErrorEnum;
import com.gugugu.haochat.common.domain.enums.user.ItemTypeEnum;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.event.UserRegisterEvent;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.common.utils.sensitive.SensitiveWordBs;
import com.gugugu.haochat.user.dao.UserBackpackDAO;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.entity.UserBackpack;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.domain.vo.resp.UserInfoCache;
import com.gugugu.haochat.user.domain.vo.resp.UserSearchRespVO;
import com.gugugu.haochat.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDAO userDAO;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserBackpackDAO userBackpackDAO;
    @Resource
    private SensitiveWordBs sensitiveWordBs;
    @Resource
    private UserCache userCache;
    @Resource
    private RoomFriendDAO roomFriendDAO;
    @Override
    public void register(String openId) {
        User user = User.builder().openId(openId).build();
        userDAO.save(user);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,user));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUsername(Long uid, String username) {
        //是否拥有改名卡
        long updateNameCard = Long.parseLong(ItemTypeEnum.MODIFY_NAME_CARD.getType().toString());
        List<UserBackpack> validByItemIds = userBackpackDAO.getValidByItemIds(uid, Collections.singletonList(updateNameCard));
        boolean isOwned = CollectionUtil.isNotEmpty(validByItemIds);
        AssertUtil.isTrue(isOwned, UserErrorEnum.NOT_ENOUGH_UPDATE_NAME_COUNT.getMsg());
        //判断用户名是否被使用
        User user = userDAO.getByUsername(username);
        AssertUtil.isEmpty(user, UserErrorEnum.USERNAME_IS_USED.getMsg());
        //判断是否有敏感词
        boolean isTrue = sensitiveWordBs.hasSensitiveWord(username);
        AssertUtil.isFalse(isTrue, ChatErrorEnum.SENSITIVE_NAME.getMsg());
        //更改用户名
        return userDAO.updateUsername(uid, username);

    }

    @Override
    public List<UserInfoCache> getBatchUserInfoCache(List<Long> uidList) {
        return userCache.getBatchByUidList(uidList);
    }

    @Override
    public PageRes<UserSearchRespVO> search(Long uid, PageReq<String> pageReq) {
        Page<User> page = userDAO.getBachByNameWithAmbiguous(pageReq);
        PageRes<UserSearchRespVO> pageRes = new PageRes<>();
        pageRes.setPageSize(pageReq.getPageSize());
        pageRes.setCurrent(pageRes.getCurrent());

        List<Long> uidList = page.getRecords().stream().map(User::getId).collect(Collectors.toList());
        //标记是否为好友
        List<UserSearchRespVO> friend = roomFriendDAO.isFriend(uid,uidList);
        pageRes.setData(friend);
        pageRes.setTotal(page.getTotal());
        return pageRes;
    }
}
