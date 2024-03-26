package com.gugugu.haochat.chat.domain.vo.req.message.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojisMessageReqBody implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * url
     */
    @NotBlank
    private String url;
}
