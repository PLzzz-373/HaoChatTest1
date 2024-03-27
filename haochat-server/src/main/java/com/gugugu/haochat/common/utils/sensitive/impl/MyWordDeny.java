package com.gugugu.haochat.common.utils.sensitive.impl;

import com.gugugu.haochat.chat.dao.SensitiveWordDAO;
import com.gugugu.haochat.chat.domain.entity.SensitiveWord;
import com.gugugu.haochat.common.utils.sensitive.IWordDeny;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyWordDeny implements IWordDeny {
    @Resource
    private SensitiveWordDAO sensitiveWordDao;

    @Override
    public List<String> deny() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}
