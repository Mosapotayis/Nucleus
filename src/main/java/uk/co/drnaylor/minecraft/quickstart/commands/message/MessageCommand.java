package uk.co.drnaylor.minecraft.quickstart.commands.message;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import uk.co.drnaylor.minecraft.quickstart.api.PluginModule;
import uk.co.drnaylor.minecraft.quickstart.argumentparsers.PlayerConsoleArgument;
import uk.co.drnaylor.minecraft.quickstart.internal.CommandBase;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Modules;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Permissions;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.RunAsync;

@Permissions
@Modules(PluginModule.MESSAGES)
@RunAsync
public class MessageCommand extends CommandBase {
    private final String to = "to";
    private final String message = "message";

    @Override
    public CommandSpec createSpec() {
        return CommandSpec.builder().executor(this)
            .arguments(
                new PlayerConsoleArgument(Text.of(to)),
                GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(message)))
            ).description(Text.of("Send a message to a player")).build();
    }

    @Override
    public String[] getAliases() {
        return new String[] { "message", "m", "msg", "whisper", "w", "tell", "t" };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        boolean b = plugin.getMessageHandler().sendMessage(src, args.<CommandSource>getOne(to).get(), args.<String>getOne(message).get());
        return b ? CommandResult.success() : CommandResult.empty();
    }
}