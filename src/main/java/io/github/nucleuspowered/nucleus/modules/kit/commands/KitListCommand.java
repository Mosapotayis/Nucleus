/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.CommandPermissionHandler;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.StandardAbstractCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SubjectPermissionCache;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.kit.datamodules.KitUserDataModule;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

/**
 * Lists all the kits.
 *
 * Command Usage: /kit list Permission: plugin.kit.list.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"list", "ls"}, subcommandOf = KitCommand.class, rootAliasRegister = "kits")
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitListCommand extends StandardAbstractCommand<CommandSource> {

    @Inject private KitHandler kitConfig;
    @Inject private KitConfigAdapter kca;
    @Inject private UserDataManager userConfigLoader;

    private CommandPermissionHandler kitPermissionHandler = null;

    @Override
    public CommandResult executeCommand(final SubjectPermissionCache<CommandSource> spc, CommandContext args) throws Exception {
        final CommandSource src = spc.getSubject();
        Set<String> kits = kitConfig.getKitNames();
        if (kits.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.empty"));
            return CommandResult.empty();
        }

        // Get permission handler for the /kit command in question.
        if (kitPermissionHandler == null) {
            kitPermissionHandler = plugin.getPermissionRegistry().getPermissionsForNucleusCommand(KitCommand.class);
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        ArrayList<Text> kitText = Lists.newArrayList();

        final KitUserDataModule user =
                src instanceof Player ? userConfigLoader.get(((Player)src).getUniqueId()).get().get(KitUserDataModule.class) : null;

        // Only show kits that the user has permission for, if needed. This is the permission "plugin.kits.<kit>".
        kitConfig.getKitNames().stream()
            .filter(kit -> !kca.getNodeOrDefault().isSeparatePermissions() ||
                    src.hasPermission(PermissionRegistry.PERMISSIONS_PREFIX + "kits." + kit.toLowerCase()))
            .forEach(kit -> kitConfig.getKit(kit).ifPresent(k -> kitText.add(createKit(spc, user, kit, k))));

        PaginationList.Builder paginationBuilder = paginationService.builder().contents(kitText)
                .title(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.kits")).padding(Text.of(TextColors.GREEN, "-"));
        paginationBuilder.sendTo(src);

        return CommandResult.success();
    }

    private Text createKit(SubjectPermissionCache<CommandSource> source, KitUserDataModule user, String kitName, Kit kitObj) {
        Text.Builder tb = Text.builder(kitName);

        if (user != null && Util.getKeyIgnoreCase(user.getKitLastUsedTime(), kitName).isPresent()) {
            // If one time used...
            if (kitObj.isOneTime() && !kitPermissionHandler.testSuffix(source, "exempt.onetime")) {
                return tb.color(TextColors.RED)
                        .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.onetime", kitName)))
                        .style(TextStyles.STRIKETHROUGH).build();
            }

            // If an intervalOld is used...
            Duration interval = kitObj.getInterval();
            if (!interval.isZero() && !kitPermissionHandler.testCooldownExempt(source)) {

                // Get the next time the kit can be used.
                Instant next = Util.getValueIgnoreCase(user.getKitLastUsedTime(), kitName).get().plus(interval);
                if (next.isAfter(Instant.now())) {
                    // Get the time to next usage.
                    String time = Util.getTimeToNow(next);
                    return tb.color(TextColors.RED)
                            .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.interval", kitName, time)))
                            .style(TextStyles.STRIKETHROUGH).build();
                }
            }
        }

        // Can use.
        Text.Builder builder = tb.color(TextColors.AQUA).onClick(TextActions.runCommand("/kit " + kitName))
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.text", kitName)))
                .style(TextStyles.ITALIC);
        if (kitObj.getCost() > 0 && plugin.getEconHelper().economyServiceExists() && !kitPermissionHandler.testCostExempt(source)) {
            builder = Text.builder().append(builder.build())
                .append(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.list.cost", plugin.getEconHelper().getCurrencySymbol(kitObj.getCost())));
        }

        return builder.build();
    }
}
