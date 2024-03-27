package com.gugugu.haochat.user.service.adapter;

import com.gugugu.haochat.common.domain.enums.YesOrNoEnum;
import com.gugugu.haochat.user.domain.entity.ItemConfig;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.vo.resp.BadgeBatchResp;

import java.util.List;
import java.util.stream.Collectors;

public class UserBackpackAdapter {

    public static List<BadgeBatchResp> buildBatchBadgesByUid(User user, List<Long> userOwnedBadgeIds, List<ItemConfig> badges) {
        Long itemIdOwned = user.getItemId();
        return badges.stream().map(badge ->{
            Long itemId = badge.getId();
            int obtains = YesOrNoEnum.NO.getStatus();
            int wearing = YesOrNoEnum.NO.getStatus();
            if(itemId.equals(itemIdOwned)){
                wearing = YesOrNoEnum.YES.getStatus();
            }
            if(userOwnedBadgeIds.contains(itemId)){
                obtains = YesOrNoEnum.YES.getStatus();
            }
            return BadgeBatchResp
                    .builder()
                    .id(itemId)
                    .img(badge.getImg())
                    .obtain(obtains)
                    .wearing(wearing)
                    .describe(badge.getDescribe())
                    .build();
        }).collect(Collectors.toList());
    }
}
