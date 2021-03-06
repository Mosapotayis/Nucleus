/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.List;

import javax.annotation.Nullable;

@NonnullByDefault
public class SelectorWrapperArgument<T extends Entity> extends CommandElement {

    private final CommandElement wrappedElement;
    private final Class<T> entityFilter;

    public SelectorWrapperArgument(CommandElement wrappedElement, final Class<T> selectorFilter) {
        super(wrappedElement.getKey());
        this.wrappedElement = wrappedElement;
        this.entityFilter = selectorFilter;
    }

    public static SelectorWrapperArgument<Player> nicknameSelector(@Nullable Text key, NicknameArgument.UnderlyingType type) {
        return nicknameSelector(key, type, true, Player.class);
    }

    public static <S extends Entity> SelectorWrapperArgument<S> nicknameSelector(@Nullable Text key, NicknameArgument.UnderlyingType type,
            boolean onlyOne, Class<S> selectorFilter) {
        return new SelectorWrapperArgument<>(new NicknameArgument(key, type, onlyOne), selectorFilter);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        String a = args.peek();
        if (a.startsWith("@")) {
            // Time to try to eek it all out.
            Selector.parse(a).resolve(source).stream().filter(entityFilter::isInstance)
                    .forEach(x ->
                            context.putArg(getKey(), x)
                    );

            args.next();

            if (context.hasAny(getKey())) {
                return;
            }

            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.selector.notarget"));
        }

        wrappedElement.parse(source, args, context);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return wrappedElement.complete(src, args, context);
    }

    @Override
    public Text getUsage(CommandSource src) {
        return wrappedElement.getUsage(src);
    }
}
