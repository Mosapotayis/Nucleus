/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.nucleusdata;

import io.github.nucleuspowered.nucleus.api.Stable;

import java.util.Optional;

/**
 * Represents a warp.
 */
@Stable
public interface Warp extends NamedLocation {

    /**
     * Gets the category for this warp, if it exists.
     *
     * @return The category name.
     */
    Optional<String> getCategory();

    /**
     * Gets the cost of this warp, if the warp has a cost.
     *
     * @return The cost.
     */
    Optional<Double> getCost();
}
