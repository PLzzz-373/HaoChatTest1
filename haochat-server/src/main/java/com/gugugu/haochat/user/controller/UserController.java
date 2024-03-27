package com.gugugu.haochat.user.controller;

import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.utils.ResultUtil;
import com.gugugu.haochat.user.domain.vo.resp.BadgeBatchResp;
import com.gugugu.haochat.user.service.ItemConfigService;
import com.gugugu.haochat.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/user")
@RestController
@Slf4j
@Api(tags = "用户模块接口")
public class UserController {
    @Resource
    private ItemConfigService itemConfigService;
    @Resource
    private UserService userService;
    @GetMapping("badge")
    @ApiOperation("获取徽章")
    public ResultUtil<List<BadgeBatchResp>> listBadge(@Valid PageReq<Object> pageReq){
        Long uid = RequestHolderDTO.get().getUid();
        List<BadgeBatchResp> listResultUtil = itemConfigService.listBadge(uid, pageReq);
        return ResultUtil.success(listResultUtil);
    }

    @PutMapping("username")
    @ApiOperation("用户名修改")
    public ResultUtil<Boolean> updateUsername(String username){
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(userService.updateUsername(uid, username));
    }
}
