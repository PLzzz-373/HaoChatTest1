package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.group.UpdateGroupInfoReq;

public interface RoomGroupService {
    Boolean updateGroupInfo(Long uid, UpdateGroupInfoReq updateGroupInfoReq);

}
