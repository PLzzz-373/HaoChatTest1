package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberResp;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMemberCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.service.ChatService;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/chat")
@Api(tags = "消息相关接口")
public class ChatController {
    @Resource
    private ChatService chatService;

    @PostMapping("send")
    @ApiOperation("发送消息")
    public ResultUtil<ChatMessageResp> send(@Valid @RequestBody ChatMessageReq req){
        Long uid = RequestHolderDTO.get().getUid();
        return chatService.send(uid, req);
    }

    @GetMapping("member/list")
    public ResultUtil<CursorPageBaseResp<ChatMemberResp, String>> listMember(@Valid ChatMemberCursorReq req){
        Long uid = RequestHolderDTO.get().getUid();
        CursorPageBaseResp<ChatMemberResp,String> resp =chatService.listMember(uid,req);
        return ResultUtil.success(resp);
    }
}
