package com.gugugu.haochat.chat.domain.vo.req.contact;

import com.gugugu.haochat.common.domain.vo.req.CursorPageBaseReq;
import lombok.*;

import java.util.Date;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChatContactCursorReq extends CursorPageBaseReq<String> {

}
