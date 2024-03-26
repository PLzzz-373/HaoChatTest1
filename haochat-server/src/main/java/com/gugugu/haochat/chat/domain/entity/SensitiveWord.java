package com.gugugu.haochat.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="sensitive_word")
@Data
public class SensitiveWord implements Serializable {
    /**
     * 敏感词
     */
    @TableField(value = "word")
    private String word;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
