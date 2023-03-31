package dev.booky.launchplates.listener;
// Created by booky10 in LaunchPlates (01:06 01.04.23)

import dev.booky.launchplates.LaunchPlateManager;
import dev.booky.launchplates.events.LaunchPlateUseEvent;
import dev.booky.launchplates.util.LaunchPlateConfig.LaunchPlate;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LaunchListener implements Listener {

    private final LaunchPlateManager manager;

    public LaunchListener(LaunchPlateManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getAction() != Action.PHYSICAL) {
            return;
        }
        if (!Tag.PRESSURE_PLATES.isTagged(event.getClickedBlock().getType())) {
            return;
        }

        LaunchPlate plate = this.manager.getLaunchPlate(event.getClickedBlock());
        if (plate == null) {
            return;
        }

        Player player = event.getPlayer();
        LaunchPlateUseEvent launchEvent = new LaunchPlateUseEvent(player,
                event.getClickedBlock(), plate.getLaunchVelocity());

        if (System.currentTimeMillis() - this.manager.getLastLaunchUse(player) < 1000L) {
            launchEvent.setCancelled(true);
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        if (!launchEvent.callEvent()) {
            return;
        }

        this.manager.setLastLaunchUse(player);
        player.setVelocity(player.getVelocity().add(launchEvent.getLaunchVelocity()));
    }

}
