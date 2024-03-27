package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.contact.ChatContactCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatReadMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ChatContactCursorResp;
import com.gugugu.haochat.chat.service.ContactService;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/contact")
@Api(tags = "会话相关接口")
public class ContactController {
    @Resource
    private ContactService contactService;

    @GetMapping("list")
    @ApiOperation("获取会话列表")
    public ResultUtil<CursorPageBaseResp<ChatContactCursorResp, Date>> listContact(@Valid ChatContactCursorReq request) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(contactService.listContact(uid, request));
    }

    @GetMapping
    @ApiOperation("获取会话信息")
    public ResultUtil<ChatContactCursorResp> getContact(Long roomId) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(contactService.getContact(uid, roomId));
    }

    @PostMapping("read-message")
    @ApiOperation("用户已读详情上报")
    public ResultUtil<Boolean> readMessage(@Valid @RequestBody ChatReadMessageReq request) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(contactService.readMessage(uid, request));
    }
}
