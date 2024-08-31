package com.ztrolix.zlibs;

import java.util.Set;
import java.util.UUID;

public interface RewardedPlayersProvider {
    Set<UUID> getRewardedPlayers();

    void removeRewardedPlayer(UUID player);

    int rewardedPlayersSize();

    boolean isEmpty();
}