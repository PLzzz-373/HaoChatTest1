package com.gugugu.haochat.user.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.common.utils.ResultUtil;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.domain.vo.resp.UserInfoCache;
import com.gugugu.haochat.user.domain.vo.resp.UserSearchRespVO;
import com.gugugu.haochat.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/user")
public class UserCommonController {
    @Resource
    private UserService userService;

    @GetMapping
    @ApiOperation("批量获取缓存")
    public ResultUtil<List<UserInfoCache>> getBatchUserInfoCache(String uidList){
        List<Long> list = CollectionUtil.toList(uidList.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
        return ResultUtil.success(userService.getBatchUserInfoCache(list));
    }

    @GetMapping("search")
    @ApiOperation("搜索用户")
    public ResultUtil<PageRes<UserSearchRespVO>> search(PageReq<String> pageReq){
        Long uid = RequestHolderDTO.get().getUid();
        return ResultUtil.success(userService.search(uid,pageReq));
    }
}
