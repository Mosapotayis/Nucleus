/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.config;

import io.github.nucleuspowered.nucleus.configurate.annotations.Default;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.regex.Pattern;

@ConfigSerializable
public class NicknameConfig {

    @Setting(value = "min-nickname-length", comment = "loc:config.nicknames.min")
    private int minNicknameLength = 3;

    @Setting(value = "max-nickname-length", comment = "loc:config.nicknames.max")
    private int maxNicknameLength = 20;

    @Setting(value = "prefix", comment = "loc:config.nicknames.prefix")
    private String prefix = "&b~";

    @Default("[a-zA-Z0-9_]+")
    @Setting(value = "pattern", comment = "loc:config.nicknames.pattern")
    private Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+");

    public int getMinNicknameLength() {
        return minNicknameLength;
    }

    public int getMaxNicknameLength() {
        return maxNicknameLength;
    }

    public String getPrefix() {
        return prefix;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
