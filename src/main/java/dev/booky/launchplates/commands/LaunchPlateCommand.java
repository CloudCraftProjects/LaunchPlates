package dev.booky.launchplates.commands;
// Created by booky10 in CloudCore (11:18 14.03.23)

import dev.booky.launchplates.LaunchPlateManager;
import dev.booky.launchplates.util.LaunchPlateConfig.LaunchPlate;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.WorldArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

public final class LaunchPlateCommand {

    private final LaunchPlateManager manager;

    private LaunchPlateCommand(LaunchPlateManager manager) {
        this.manager = manager;
    }

    public static void create(LaunchPlateManager manager) {
        LaunchPlateCommand command = new LaunchPlateCommand(manager);
        command.unregister();
        command.register();
    }

    private WrapperCommandSyntaxException fail(Component message) {
        return CommandAPIBukkit.failWithAdventureComponent(this.manager.getPrefix()
                .append(message.colorIfAbsent(NamedTextColor.RED)));
    }

    private void success(CommandSender sender, Component message) {
        sender.sendMessage(this.manager.getPrefix()
                .append(message.colorIfAbsent(NamedTextColor.YELLOW)));
    }

    private void unregister() {
        CommandAPI.unregister("launchplate", true);
    }

    private void register() {
        new CommandTree("launchplate")
                .withPermission("launchplates.command")
                .then(new LiteralArgument("list")
                        .withPermission("launchplates.command.list")
                        .executesNative(this::listPlates))
                .then(new LiteralArgument("create")
                        .withPermission("launchplates.command.create")
                        .then(new LocationArgument("block", LocationType.BLOCK_POSITION).setOptional(true)
                                .then(new WorldArgument("dimension").setOptional(true)
                                        .executesNative(this::createPlate))))
                .then(new LiteralArgument("delete")
                        .withPermission("launchplates.command.delete")
                        .then(new LocationArgument("block", LocationType.BLOCK_POSITION).setOptional(true)
                                .then(new WorldArgument("dimension").setOptional(true)
                                        .executesNative(this::deletePlate))))
                .then(new LiteralArgument("boost")
                        .withPermission("launchplates.command.boost")
                        .then(new FloatArgument("velocityX", -3.8f, 3.8f)
                                .then(new FloatArgument("velocityY", -3.8f, 3.8f)
                                        .then(new FloatArgument("velocityZ", -3.8f, 3.8f)
                                                .then(new LocationArgument("block", LocationType.BLOCK_POSITION).setOptional(true)
                                                        .then(new WorldArgument("dimension").setOptional(true)
                                                                .executesNative(this::setPlateBoost)))))))
                .then(new LiteralArgument("reload")
                        .withPermission("launchplates.command.reload-config")
                        .executesNative(this::reloadConfig))
                .register();
    }

    private void listPlates(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        Set<LaunchPlate> plates = this.manager.getConfig().getPlates();
        if (plates.isEmpty()) {
            throw this.fail(Component.translatable("launchplates.command.list.none"));
        }

        ComponentBuilder<?, ?> builder = Component.translatable()
                .key("launchplates.command.list.header");

        boolean deletePerms = sender.hasPermission("launchplates.command.delete");
        boolean teleportPerms = sender.hasPermission("minecraft.command.teleport");

        for (LaunchPlate plate : plates) {
            builder.appendNewline();

            Component deleteComp = Component.empty();
            if (deletePerms) {
                deleteComp = Component.translatable("launchplates.command.list.delete.button", NamedTextColor.RED)
                        .hoverEvent(Component.translatable("launchplates.command.list.delete.warning", NamedTextColor.RED))
                        .clickEvent(ClickEvent.callback(clicker -> {
                            if (clicker == sender.getCaller()) {
                                manager.updateConfig(config -> config.getPlates().remove(plate));
                                success(sender, Component.translatable("launchplates.command.delete.success"));
                            }
                        }, opts -> opts.lifetime(Duration.ofMinutes(10))));
            }

            Block block = plate.getBlock();
            Component entryComp = Component.translatable("launchplates.command.list.entry", NamedTextColor.YELLOW,
                    Component.text(block.getX()), Component.text(block.getY()), Component.text(block.getZ()),
                    Component.text(block.getWorld().getKey().asString()), deleteComp);

            if (teleportPerms) {
                Location blockLocation = block.getLocation().toCenterLocation();
                entryComp = entryComp
                        .hoverEvent(Component.translatable("launchplates.command.list.tp-hint", NamedTextColor.AQUA))
                        .clickEvent(ClickEvent.callback(clicker -> {
                            if (this.manager.getLaunchPlate(block) == null) {
                                return;
                            }
                            if (clicker == sender.getCaller() && clicker instanceof Player player) {
                                blockLocation.setYaw(player.getLocation().getYaw());
                                blockLocation.setPitch(player.getLocation().getPitch());
                                player.teleport(blockLocation, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH);
                            }
                        }, opts -> opts.uses(ClickCallback.UNLIMITED_USES).lifetime(Duration.ofMinutes(10))));
            }

            builder.append(Component.space()).append(entryComp);
        }

        this.success(sender, builder.build());
    }

    private void createPlate(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        Vector pos = args.<Location>getOptionalUnchecked("block").orElseGet(sender::getLocation).toVector();
        World world = args.<World>getOptionalUnchecked("dimension").orElseGet(sender::getWorld);
        Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        if (this.manager.getLaunchPlate(block) != null) {
            throw this.fail(Component.translatable("launchplates.command.create.already"));
        }
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            throw this.fail(Component.translatable("launchplates.command.create.not-plate"));
        }

        this.manager.updateConfig(config -> config.getPlates().add(new LaunchPlate(block)));
        this.success(sender, Component.translatable("launchplates.command.create.success"));
    }

    private void deletePlate(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        Vector pos = args.<Location>getOptionalUnchecked("block").orElseGet(sender::getLocation).toVector();
        World world = args.<World>getOptionalUnchecked("dimension").orElseGet(sender::getWorld);
        Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        LaunchPlate plate = this.manager.getLaunchPlate(block);
        if (plate == null) {
            throw this.fail(Component.translatable("launchplates.command.delete.not-found"));
        }

        this.manager.updateConfig(config -> config.getPlates().remove(plate));
        this.success(sender, Component.translatable("launchplates.command.delete.success"));
    }

    private void setPlateBoost(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        Vector pos = args.<Location>getOptionalUnchecked("block").orElseGet(sender::getLocation).toVector();
        World world = args.<World>getOptionalUnchecked("dimension").orElseGet(sender::getWorld);
        Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        LaunchPlate plate = this.manager.getLaunchPlate(block);
        if (plate == null) {
            throw this.fail(Component.translatable("launchplates.command.boost.not-found"));
        }


        double velocityX = Objects.requireNonNull(args.<Float>getUnchecked("velocityX"));
        double velocityY = Objects.requireNonNull(args.<Float>getUnchecked("velocityY"));
        double velocityZ = Objects.requireNonNull(args.<Float>getUnchecked("velocityZ"));
        velocityX = Math.round(velocityX * 100d) / 100d;
        velocityY = Math.round(velocityY * 100d) / 100d;
        velocityZ = Math.round(velocityZ * 100d) / 100d;

        plate.getLaunchVelocity().setX(velocityX);
        plate.getLaunchVelocity().setY(velocityY);
        plate.getLaunchVelocity().setZ(velocityZ);
        this.manager.saveConfig();

        this.success(sender, Component.translatable("launchplates.command.boost.success",
                Component.text(velocityX + ":" + velocityY + ":" + velocityZ, NamedTextColor.WHITE)));
    }

    private void reloadConfig(NativeProxyCommandSender sender, CommandArguments args) {
        this.manager.reloadConfig();
        this.success(sender, Component.translatable("launchplates.command.reload"));
    }
}
