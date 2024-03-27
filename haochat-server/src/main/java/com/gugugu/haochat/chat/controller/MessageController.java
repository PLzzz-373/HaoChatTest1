package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.RevokeMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.service.ChatService;
import com.gugugu.haochat.chat.service.MessageService;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/message")
@Api(tags = "消息列表相关模块")
public class MessageController {
    @Resource
    private ChatService chatService;
    @Resource
    private MessageService messageService;
    @GetMapping("list")
    @ApiOperation("消息列表")
    public ResultUtil<CursorPageBaseResp<ChatMessageResp,String>> listMessage(@Valid ChatMessageCursorReq req){
        Long uid = RequestHolderDTO.get().getUid();
        CursorPageBaseResp<ChatMessageResp,String> messageList = chatService.listMessage(uid,req);
        return ResultUtil.success(messageList);
    }

    @PutMapping("revoke")
    @ApiOperation("撤回消息")
    public ResultUtil<Boolean> revoke(@RequestBody @Valid RevokeMessageReq req){
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(messageService.revoke(uid,req));
    }
}
