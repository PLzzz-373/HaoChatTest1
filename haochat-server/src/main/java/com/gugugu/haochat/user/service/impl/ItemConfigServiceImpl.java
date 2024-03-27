package com.gugugu.haochat.user.service.impl;

import com.gugugu.haochat.common.cache.ItemCache;
import com.gugugu.haochat.common.domain.enums.user.ItemTypeEnum;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.user.dao.UserBackpackDAO;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.ItemConfig;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.entity.UserBackpack;
import com.gugugu.haochat.user.domain.vo.resp.BadgeBatchResp;
import com.gugugu.haochat.user.service.ItemConfigService;
import com.gugugu.haochat.user.service.adapter.UserBackpackAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ItemConfigServiceImpl implements ItemConfigService {
    @Resource
    private ItemCache itemCache;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserBackpackDAO userBackpackDAO;
    @Override
    public List<BadgeBatchResp> listBadge(Long uid, PageReq<Object> pageReq) {
        //1获取徽章
        List<ItemConfig> badges = itemCache.getByType(pageReq.getCurrent(), ItemTypeEnum.BADGE.getType());
        //2获取用户已经拥有的勋章id
        List<Long> list = badges.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<Long> userOwnedBadgeIds = userBackpackDAO.getValidByItemIds(uid, list).stream().map(UserBackpack::getItemId).collect(Collectors.toList());
        //3构建用户已拥有的徽章进行标记
        User user = userDAO.getById(uid);
        return UserBackpackAdapter.buildBatchBadgesByUid(user,userOwnedBadgeIds,badges);
    }
}
