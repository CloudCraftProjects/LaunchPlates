package dev.booky.launchplates.events;
// Created by booky10 in CloudCore (15:45 31.03.23)

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LaunchPlateUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Block plateBlock;
    private Vector launchVelocity;
    private boolean cancelled = false;

    public LaunchPlateUseEvent(Player player, Block plateBlock, Vector launchVelocity) {
        super(player);
        this.plateBlock = plateBlock;
        this.launchVelocity = launchVelocity;
    }

    public Block getPlateBlock() {
        return this.plateBlock;
    }

    public Vector getLaunchVelocity() {
        return this.launchVelocity;
    }

    public void setLaunchVelocity(Vector launchVelocity) {
        this.launchVelocity = launchVelocity;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
