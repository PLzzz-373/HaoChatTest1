package com.gugugu.haochat.user.domain.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageRes<DataType> {
    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 总数
     */
    private Long total;

    /**
     * 数据
     */
    private List<DataType> data;
}
