/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.handler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.dataservices.modular.ModularUserService;
import io.github.nucleuspowered.nucleus.iapi.data.MuteData;
import io.github.nucleuspowered.nucleus.iapi.service.NucleusMuteService;
import io.github.nucleuspowered.nucleus.modules.mute.datamodules.MuteUserDataModule;
import org.spongepowered.api.entity.living.player.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MuteHandler implements NucleusMuteService {

    private final NucleusPlugin nucleus;
    @Inject private UserDataManager ucl;

    private boolean globalMuteEnabled = false;
    private List<UUID> voicedUsers = Lists.newArrayList();

    public MuteHandler(NucleusPlugin nucleus) {
        this.nucleus = nucleus;
    }

    @Override
    public boolean isMuted(User user) {
        return getPlayerMuteData(user).isPresent();
    }

    @Override
    public Optional<MuteData> getPlayerMuteData(User user) {
        Optional<ModularUserService> nu = ucl.get(user);
        if (nu.isPresent()) {
            return nu.get().get(MuteUserDataModule.class).getMuteData();
        }

        return Optional.empty();
    }

    @Override
    public boolean mutePlayer(User user, MuteData data) {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(data);

        Optional<ModularUserService> nu = ucl.get(user);
        if (!nu.isPresent()) {
            return false;
        }

        ModularUserService u = nu.get();
        if (user.isOnline() && data.getTimeFromNextLogin().isPresent() && !data.getEndTimestamp().isPresent()) {
            data = new MuteData(data.getMuter(), data.getReason(), Instant.now().plus(data.getTimeFromNextLogin().get()));
        }

        u.get(MuteUserDataModule.class).setMuteData(data);
        return true;
    }

    @Override
    public boolean unmutePlayer(User user) {
        if (isMuted(user)) {
            Optional<ModularUserService> o = ucl.get(user);
            if (o.isPresent()) {
                o.get().get(MuteUserDataModule.class).removeMuteData();
                return true;
            }
        }

        return false;
    }

    public boolean isGlobalMuteEnabled() {
        return globalMuteEnabled;
    }

    public void setGlobalMuteEnabled(boolean globalMuteEnabled) {
        if (this.globalMuteEnabled != globalMuteEnabled) {
            voicedUsers.clear();
        }

        this.globalMuteEnabled = globalMuteEnabled;
    }

    public boolean isVoiced(UUID uuid) {
        return voicedUsers.contains(uuid);
    }

    public void addVoice(UUID uuid) {
        voicedUsers.add(uuid);
    }

    public void removeVoice(UUID uuid) {
        voicedUsers.remove(uuid);
    }
}
