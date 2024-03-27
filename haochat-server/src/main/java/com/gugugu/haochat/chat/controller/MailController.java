package com.gugugu.haochat.chat.controller;

import com.gugugu.haochat.chat.domain.vo.req.message.MessageOperationReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.MessageResp;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.utils.ResultUtil;
import com.gugugu.haochat.user.domain.entity.UserApply;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.service.UserApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/mail")
@Api(tags = "用户信箱相关接口")
public class MailController {
    @Resource
    private UserApplyService userApplyService;

    @GetMapping("list")
    @ApiOperation("获取用户消息")
    public ResultUtil<PageRes<MessageResp>> listMessage(PageReq<String> pageReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(userApplyService.listMessage(uid, pageReq));
    }
    @GetMapping("operation")
    @ApiOperation("消息操作")
    public ResultUtil<Boolean> operation(@Valid MessageOperationReq messageOperationReq) {
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(userApplyService.operation(uid, messageOperationReq));
    }
}
