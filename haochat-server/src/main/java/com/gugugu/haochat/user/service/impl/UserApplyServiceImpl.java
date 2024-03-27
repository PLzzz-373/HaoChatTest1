package com.gugugu.haochat.user.service.impl;

import com.gugugu.haochat.chat.domain.vo.req.message.MessageOperationReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.MessageResp;
import com.gugugu.haochat.chat.service.GroupMemberService;
import com.gugugu.haochat.chat.service.RoomFriendService;
import com.gugugu.haochat.common.domain.enums.UserApplyStatusEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.user.dao.UserApplyDAO;
import com.gugugu.haochat.user.domain.entity.UserApply;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.service.UserApplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Service
public class UserApplyServiceImpl implements UserApplyService {
    @Resource
    private UserApplyDAO userApplyDAO;
    @Resource
    private RoomFriendService roomFriendService;
    @Resource
    private GroupMemberService groupMemberService;
    @Override
    public PageRes<MessageResp> listMessage(Long uid, PageReq<String> pageReq) {
        PageRes<UserApply> userApplyPageRes = userApplyDAO.listMessage(uid, pageReq);
        return userApplyDAO.buildMessageResp(userApplyPageRes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean operation(Long uid, MessageOperationReq messageOperationReq) {

        Long id = messageOperationReq.getId();

        // 1. 判断消息是否存在且是否是该用户的
        UserApply userApply = userApplyDAO.isExist(uid, id);
        AssertUtil.isNotEmpty(userApply, ChatErrorEnum.MESSAGE_NOT_EXIST.getMsg());

        // 2. 判断消息是否已被操作
        Boolean isOperated = userApplyDAO.isOperated(uid, id);
        AssertUtil.isFalse(isOperated, ChatErrorEnum.REPEAT_OPERATED.getMsg());

        // 3. 操作消息
        this.operate(userApply, messageOperationReq.getStatus());
        return true;
    }

    private void operate(UserApply userApply, Integer status) {
        if (UserApplyStatusEnum.AGREED.getStatus().equals(status)) {
            Integer type = userApply.getType();
            switch (type) {
                case 1 :
                    // 加好友
                    roomFriendService.agreeAddFriend(userApply);
                    break;

                case 2 :
                    // 加群
                    groupMemberService.agreeAddGroup(userApply);
                    break;

                default :
                    throw new RuntimeException("未知的消息类型");

            }
        }

        Boolean isOperatedSuccess = userApplyDAO.operate(userApply.getId(), status);
        AssertUtil.isTrue(isOperatedSuccess, CommonErrorEnum.SYSTEM_ERROR.getMsg());
    }
}
