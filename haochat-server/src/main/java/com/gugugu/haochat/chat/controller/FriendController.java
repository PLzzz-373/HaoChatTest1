package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.friend.AddFriendReq;
import com.gugugu.haochat.chat.domain.vo.resp.friend.FriendResp;
import com.gugugu.haochat.chat.service.RoomFriendService;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/api/friend")
@RestController
@Api(tags = "用户好友相关接口")
public class FriendController {
    @Resource
    private RoomFriendService roomFriendService;
    @GetMapping("list")
    @ApiOperation("获取好友列表")
    public ResultUtil<List<FriendResp>> listContact() {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(roomFriendService.listFriend(uid));
    }

    @DeleteMapping("del")
    @ApiOperation("删除好友")
    public ResultUtil<Boolean> delFriend(Long friendId) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(roomFriendService.delFriend(uid, friendId), true);
    }

    @PostMapping("add-friend")
    @ApiOperation("加好友")
    public ResultUtil<Boolean> addFriend(@RequestBody AddFriendReq addFriendReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(roomFriendService.applyAddFriend(uid, addFriendReq), true);
    }
}
