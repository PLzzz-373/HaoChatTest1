package com.gugugu.haochat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.SensitiveWord;
import com.gugugu.haochat.chat.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;

@Service
public class SensitiveWordDAO extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {
}
