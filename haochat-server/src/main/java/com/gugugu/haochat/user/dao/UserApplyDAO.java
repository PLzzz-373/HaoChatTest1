package com.gugugu.haochat.user.dao;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.vo.resp.message.MessageResp;
import com.gugugu.haochat.chat.service.strategy.mail.AbstractReadStatusContentHandler;
import com.gugugu.haochat.chat.service.strategy.mail.factory.MailContentReadStatusFactory;
import com.gugugu.haochat.common.domain.enums.ReadStatusEnum;
import com.gugugu.haochat.common.domain.enums.UserApplyEnum;
import com.gugugu.haochat.common.domain.enums.UserApplyStatusEnum;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.user.domain.UserApplyExtraInfo;
import com.gugugu.haochat.user.domain.entity.UserApply;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.mapper.UserApplyMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class UserApplyDAO extends ServiceImpl<UserApplyMapper, UserApply> {
    public Boolean isApplyingFriend(Long uid, Long repliedId) {
        return this.lambdaQuery()
                .eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, repliedId)
                .or()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getUid, repliedId)
                .and(wrapper -> {
                    wrapper.eq(UserApply::getType, UserApplyEnum.FRIEND.getType())
                            .eq(UserApply::getStatus, UserApplyStatusEnum.APPLYING.getStatus());
                }).exists();
    }

    public Boolean addFriend(Long uid, Long repliedId, String msg) {
        UserApply userApply = new UserApply();
        userApply.setUid(uid);
        userApply.setType(UserApplyEnum.FRIEND.getType());
        userApply.setTargetId(repliedId);
        userApply.setMsg(msg);
        userApply.setStatus(UserApplyStatusEnum.APPLYING.getStatus());
        userApply.setReadStatus(ReadStatusEnum.UNREAD.getStatus());
        return this.save(userApply);
    }

    public Boolean applyCreateGroup(Long groupId, Long uid, List<Long> uidList, String msg) {
        List<UserApply> applyList = new ArrayList<>();
        for (Long invitedId : uidList) {
            UserApply userApply = new UserApply();
            userApply.setUid(uid);
            userApply.setType(UserApplyEnum.GROUP.getType());
            userApply.setTargetId(invitedId);
            userApply.setMsg(msg);
            userApply.setExtraInfo(new UserApplyExtraInfo(groupId));
            userApply.setStatus(UserApplyStatusEnum.APPLYING.getStatus());
            userApply.setReadStatus(ReadStatusEnum.UNREAD.getStatus());
            applyList.add(userApply);
        }
        return this.saveBatch(applyList);
    }

    public Boolean inviteGroup(Long uid, Long groupId, List<Long> uidList, String msg) {
        List<UserApply> applyList = new ArrayList<>();
        uidList.forEach(targetId -> {
            UserApply userApply = new UserApply();
            userApply.setUid(uid);
            userApply.setType(UserApplyEnum.GROUP.getType());
            userApply.setTargetId(targetId);
            userApply.setMsg(msg);
            //加群申请必须带上这个群ID
            UserApplyExtraInfo userApplyExtraInfo = new UserApplyExtraInfo();
            userApplyExtraInfo.setGroupId(groupId);
            userApply.setExtraInfo(userApplyExtraInfo);
            userApply.setStatus(UserApplyStatusEnum.APPLYING.getStatus());
            userApply.setReadStatus(ReadStatusEnum.UNREAD.getStatus());
            applyList.add(userApply);
        });
        return this.saveBatch(applyList);
    }

    public PageRes<UserApply> listMessage(Long uid, PageReq<String> pageReq) {
        Page<UserApply> userApplyPage = new Page<>(pageReq.getCurrent(), pageReq.getPageSize(), true);
        Page<UserApply> page = this.lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, Optional.of(Integer.parseInt(pageReq.getData())).orElse(ReadStatusEnum.UNREAD.getStatus()))
                .page(userApplyPage);
        PageRes<UserApply> pageRes = new PageRes<>();
        pageRes.setTotal(page.getTotal())
                .setData(page.getRecords())
                .setCurrent(page.getCurrent())
                .setPageSize(page.getSize());
        return pageRes;
    }

    public PageRes<MessageResp> buildMessageResp(PageRes<UserApply> userApplyPageRes) {
        List<MessageResp> list = userApplyPageRes.getData().stream().map(userApply -> {
            MessageResp messageResp = new MessageResp();
            messageResp.setId(userApply.getId());
            messageResp.setUid(userApply.getUid());

            // 获取消息发送者的用户名
            String content;
            AbstractReadStatusContentHandler handler = MailContentReadStatusFactory.getHandler(userApply.getReadStatus());
            if (UserApplyEnum.FRIEND.getType().equals(userApply.getType())) {
                content = handler.showContentInAddFriend(userApply);
            } else {
                content = handler.showContentInInviteGroup(userApply);
            }

            messageResp.setContent(content);
            messageResp.setMsg(userApply.getMsg());
            messageResp.setType(userApply.getType());
            return messageResp;
        }).collect(Collectors.toList());
        PageRes<MessageResp> messageRespPageRes = new PageRes<>();
        messageRespPageRes.setTotal(userApplyPageRes.getTotal())
                .setData(list)
                .setCurrent(userApplyPageRes.getCurrent())
                .setPageSize(userApplyPageRes.getPageSize());
        return messageRespPageRes;
    }

    public Boolean operate(Long id, Integer status) {
        UserApply byId = this.getById(id);
        UserApply userApply = new UserApply();
        userApply.setId(id);
        userApply.setUid(byId.getTargetId());
        userApply.setStatus(status);
        userApply.setReadStatus(ReadStatusEnum.READ.getStatus());
        return this.updateById(userApply);
    }

    public UserApply isExist(Long uid, Long id) {

        return this.lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getId, Optional.of(id).orElse(-99L))
                .one();
    }

    public Boolean isOperated(Long uid, Long id) {
        return this.lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getId, Optional.of(id).orElse(-99L))
                .eq(UserApply::getStatus, UserApplyStatusEnum.AGREED.getStatus())
                .or()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getId, Optional.of(id).orElse(-99L))
                .eq(UserApply::getStatus, UserApplyStatusEnum.REFUSE.getStatus())
                .exists();
    }
}
