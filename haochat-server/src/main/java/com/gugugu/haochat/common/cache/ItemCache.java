package com.gugugu.haochat.common.cache;

import com.gugugu.haochat.user.dao.ItemConfigDAO;
import com.gugugu.haochat.user.domain.entity.ItemConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigDAO itemConfigDao;

    /**
     * 将查询出来的类型物品进行缓存
     *
     * @param type 物品类型 1 2
     * @return 物品列表
     */
    @Cacheable(cacheNames = "item", key = "'itemsByType:' + #type + #current")
    public List<ItemConfig> getByType(Long current, Integer type) {
        return itemConfigDao.getByType(current, type);
    }

    /**
     * 缓存每个物品
     *
     * @param itemId 物品ID
     * @return 物品信息
     */
    @Cacheable(cacheNames = "item", key = "'item:' + #itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
