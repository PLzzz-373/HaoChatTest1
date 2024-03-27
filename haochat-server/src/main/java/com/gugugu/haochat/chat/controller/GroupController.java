package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.group.*;
import com.gugugu.haochat.chat.service.GroupMemberService;
import com.gugugu.haochat.chat.service.RoomGroupService;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RequestMapping("/api/group")
@RestController
@Api(tags = "群聊相关接口")
public class GroupController {
    @Resource
    private GroupMemberService groupMemberService;
    @Resource
    private RoomGroupService roomGroupService;
    @DeleteMapping("exit")
    @ApiOperation("退出群聊")
    public ResultUtil<Boolean> exitGroup(Long groupId) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(groupMemberService.exitGroup(uid, groupId), true);
    }
    @PostMapping("create")
    @ApiOperation("创建群聊")
    public ResultUtil<Boolean> creatGroup(@Valid @RequestBody CreateGroupReq createGroupReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(groupMemberService.createGroup(uid, createGroupReq), true);
    }
    @PostMapping("invite")
    @ApiOperation("邀请入群")
    public ResultUtil<Boolean> inviteGroup(@Valid @RequestBody InvitAddGroupReq inviteAddGroupReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(groupMemberService.inviteGroup(uid, inviteAddGroupReq));
    }
    @PutMapping("admin/add")
    @ApiOperation("添加管理员")
    public ResultUtil<Boolean> addAdmin(@Valid @RequestBody AddAdminReq addAdminReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(groupMemberService.addAdmin(uid, addAdminReq));
    }
    @PutMapping("admin/update")
    @ApiOperation("删除管理员")
    public ResultUtil<Boolean> delAdmin(@Valid @RequestBody DelAdminReq delAdminReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(groupMemberService.delAdmin(uid, delAdminReq));
    }

    @PutMapping("info/update")
    @ApiOperation("更改群信息")
    public ResultUtil<Boolean> updateGroupInfo(@Valid @RequestBody UpdateGroupInfoReq updateGroupInfoReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(roomGroupService.updateGroupInfo(uid, updateGroupInfoReq));
    }
}
