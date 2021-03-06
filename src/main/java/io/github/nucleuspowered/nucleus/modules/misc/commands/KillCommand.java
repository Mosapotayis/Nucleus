/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.argumentparsers.NicknameArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import java.util.Collection;

@Permissions(supportsSelectors = true)
@RegisterCommand("kill")
public class KillCommand extends AbstractCommand<CommandSource> {

    private final String key = "subject";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            SelectorWrapperArgument.nicknameSelector(Text.of(key), NicknameArgument.UnderlyingType.PLAYER, false, Entity.class)
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Collection<Entity> entities = args.getAll(key);

        int entityKillCount = 0;
        int playerKillCount = 0;
        for (Entity x : entities) {
            if (x instanceof Player) {
                Player pl = (Player)x;
                GameMode gm = pl.gameMode().getDirect().orElseGet(() -> pl.gameMode().getDefault());
                if (gm != GameModes.SURVIVAL && gm != GameModes.NOT_SET) {
                    throw ReturnMessageException.fromKey("command.kill.wronggm", pl.getName());
                }
            }

            x.offer(Keys.HEALTH, 0d);
            entityKillCount++;

            if (x instanceof Player) {
                playerKillCount++;
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.killed",
                        Nucleus.getNucleus().getNameUtil().getSerialisedName((Player)x)));
                ((Player)x).sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.killedby", src.getName()));
            }
        }

        if (entityKillCount > playerKillCount) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.overall", String.valueOf(entityKillCount),
                    String.valueOf(playerKillCount)));
        }

        return CommandResult.success();
    }
}
