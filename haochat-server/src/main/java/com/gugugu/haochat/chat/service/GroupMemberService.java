package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.group.AddAdminReq;
import com.gugugu.haochat.chat.domain.vo.req.group.CreateGroupReq;
import com.gugugu.haochat.chat.domain.vo.req.group.DelAdminReq;
import com.gugugu.haochat.chat.domain.vo.req.group.InvitAddGroupReq;
import com.gugugu.haochat.user.domain.entity.UserApply;

public interface GroupMemberService {
    String exitGroup(Long uid, Long groupId);

    String createGroup(Long uid, CreateGroupReq createGroupReq);

    Boolean inviteGroup(Long uid, InvitAddGroupReq inviteAddGroupReq);

    Boolean addAdmin(Long uid, AddAdminReq addAdminReq);

    Boolean delAdmin(Long uid, DelAdminReq delAdminReq);

    void agreeAddGroup(UserApply userApply);

}
