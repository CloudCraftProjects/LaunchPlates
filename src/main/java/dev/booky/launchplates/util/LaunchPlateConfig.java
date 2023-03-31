package dev.booky.launchplates.util;
// Created by booky10 in CloudCore (14:51 14.03.23)

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

// Can't be final because of object mapping
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class LaunchPlateConfig {

    private Set<LaunchPlate> plates = new HashSet<>();

    @ConfigSerializable
    public static final class LaunchPlate {

        private Block block = null;
        private Vector launchVelocity = new Vector(0d, 2d, 0d);

        private LaunchPlate() {
        }

        public LaunchPlate(Block block) {
            this.block = block;
        }

        public LaunchPlate(Block block, Vector launchVelocity) {
            this.block = block;
            this.launchVelocity = launchVelocity;
        }

        public Block getBlock() {
            return this.block;
        }

        public Vector getLaunchVelocity() {
            return this.launchVelocity;
        }
    }

    private LaunchPlateConfig() {
    }

    public Set<LaunchPlate> getPlates() {
        return this.plates;
    }
}
