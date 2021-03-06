/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.ItemStackSnapshotSerialiser;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class KitDataNode implements Kit {

    @Setting private List<ConfigurationNode> stacks = Lists.newArrayList();

    /**
     * This is in seconds to be consistent with the rest of the plugin.
     */
    @Setting private long interval = 0;

    @Setting private double cost = 0;

    @Setting private boolean oneTime = false;

    @Override
    public List<ItemStackSnapshot> getStacks() {
        return ItemStackSnapshotSerialiser.INSTANCE.deserializeList(stacks);
    }

    @Override
    public Kit setStacks(List<ItemStackSnapshot> stacks) {
        this.stacks = ItemStackSnapshotSerialiser.INSTANCE.serializeList(stacks);
        return this;
    }

    @Override
    public Duration getInterval() {
        return Duration.ofSeconds(interval);
    }

    @Override
    public Kit setInterval(Duration interval) {
        this.interval = interval.getSeconds();
        return this;
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public Kit setCost(double cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public boolean isOneTime() {
        return this.oneTime;
    }

    @Override
    public Kit setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
        return this;
    }

    @Override public Kit updateKitInventory(Inventory inventory) {
        List<Inventory> slots = Lists.newArrayList(inventory.slots());
        final List<ItemStackSnapshot> stacks = slots.stream().filter(x -> x.peek().isPresent()).map(x -> x.peek().get().createSnapshot()).collect(
            Collectors.toList());

        // Add all the stacks into the kit list.
        setStacks(stacks);
        return this;
    }

    @Override public Kit updateKitInventory(Player player) {
        return updateKitInventory(Util.getStandardInventory(player));
    }
}
