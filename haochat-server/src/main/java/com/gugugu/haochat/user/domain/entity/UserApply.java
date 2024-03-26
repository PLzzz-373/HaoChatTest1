package com.gugugu.haochat.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.gugugu.haochat.user.domain.UserApplyExtraInfo;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "user_apply", autoResultMap = true)
public class UserApply implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人uid
     */
    @TableField(value = "uid")
    private Long uid;

    /**
     * 申请类型 1加好友 2加群
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 接收人uid
     */
    @TableField(value = "target_id")
    private Long targetId;

    /**
     * 申请信息
     */
    @TableField(value = "msg")
    private String msg;

    /**
     * 额外信息  例如"{ groupId: xxx }"
     */
    @TableField(value = "extra_info", typeHandler = JacksonTypeHandler.class)
    private UserApplyExtraInfo extraInfo;

    /**
     * 申请状态 1待审批 2同意 3拒绝
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 阅读状态 1未读 2已读
     */
    @TableField(value = "read_status")
    private Integer readStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
