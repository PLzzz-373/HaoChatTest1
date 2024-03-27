package com.gugugu.haochat.common.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class PageReq<DataType> {
    /**
     * 当前页
     */
    @NotNull
    @Min(value = 1, message = "最小页码为1")
    @ApiModelProperty("页面索引")
    private Long current;

    /**
     * 每页大小
     */
    @NotNull
    @Max(value = 50, message = "每页最大数量为50")
    @Min(value = 1, message = "每页最小数量为1")
    @ApiModelProperty("页面大小")
    private Long pageSize;

    /**
     * 数据
     */
    @ApiModelProperty("数据")
    private DataType data;
}
