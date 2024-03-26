package com.gugugu.haochat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.user.domain.entity.ItemConfig;
import com.gugugu.haochat.user.mapper.ItemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ItemConfigDAO extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public static final int DEFAULT_PAGE_SIZE = 10;

    public List<ItemConfig> getByType(Long current, Integer type) {
        long skip = (current - 1) * DEFAULT_PAGE_SIZE;
        return lambdaQuery()
                .eq(ItemConfig::getType, type)
                .last("limit" + skip + "," + DEFAULT_PAGE_SIZE)
                .list();
    }
}
