package com.gugugu.haochat.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BadgeBatchResp {

    @ApiModelProperty("徽章id")
    private Long id;

    @ApiModelProperty("徽章图标")
    private String img;

    @ApiModelProperty("徽章描述")
    private String describe;

    @ApiModelProperty("是否拥有 0否 1是")
    private Integer obtain;

    @ApiModelProperty("是否佩戴 0否 1是")
    private Integer wearing;
}
