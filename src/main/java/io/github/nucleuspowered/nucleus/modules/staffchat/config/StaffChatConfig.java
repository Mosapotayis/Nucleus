/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.config;

import io.github.nucleuspowered.nucleus.NameUtil;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.format.TextColor;

@ConfigSerializable
public class StaffChatConfig {

    @Setting(value = "include-standard-chat-formatting", comment = "loc:config.staffchat.includestd")
    private boolean includeStandardChatFormatting = false;

    @Setting(value = "message-template", comment = "loc:config.staffchat.template")
    private String messageTemplate = "&b[STAFF] &r{{displayname}}&b: ";

    @Setting(value = "message-colour", comment = "loc:config.staffchat.colour")
    private String messageColour = "b";

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String getMessageColour() {
        if (messageColour.isEmpty() || !messageColour.matches("^[0-9a-f]")) {
            return "b";
        }

        return messageColour.substring(0, 1);
    }

    public TextColor getColour() {
        return NameUtil.getColours().get(getMessageColour().charAt(0));
    }

    public boolean isIncludeStandardChatFormatting() {
        return includeStandardChatFormatting;
    }
}
