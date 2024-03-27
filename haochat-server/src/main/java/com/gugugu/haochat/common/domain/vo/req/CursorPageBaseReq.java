package com.gugugu.haochat.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseReq<C> {

    /**
     * 页面大小
     */
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    /**
     * 游标（初始为null，后续请求附带上次翻页的游标）
     */
    private C cursor;

    public Page plusPage() {
        return new Page(1, this.pageSize);
    }
}
