package dev.booky.launchplates;
// Created by booky10 in CloudCore (11:18 14.03.23)

import dev.booky.cloudcore.config.ConfigurateLoader;
import dev.booky.launchplates.util.LaunchPlateConfig;
import dev.booky.launchplates.util.LaunchPlateConfig.LaunchPlate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class LaunchPlateManager {

    private static final Component PREFIX = Component.text() // <gray>[<gradient:#ff6924:#ff3c19>LaunchPlates</gradient>] </gray>
            .append(Component.text("[", NamedTextColor.GRAY))
            .append(Component.text("L", TextColor.color(0xFF6924)))
            .append(Component.text("a", TextColor.color(0xFF6523)))
            .append(Component.text("u", TextColor.color(0xFF6222)))
            .append(Component.text("n", TextColor.color(0xFF5E21)))
            .append(Component.text("c", TextColor.color(0xFF5A20)))
            .append(Component.text("h", TextColor.color(0xFF561F)))
            .append(Component.text("P", TextColor.color(0xFF531F)))
            .append(Component.text("l", TextColor.color(0xFF4F1E)))
            .append(Component.text("a", TextColor.color(0xFF4B1D)))
            .append(Component.text("t", TextColor.color(0xFF471C)))
            .append(Component.text("e", TextColor.color(0xFF441B)))
            .append(Component.text("s", TextColor.color(0xFF401A)))
            .append(Component.text("] ", NamedTextColor.GRAY))
            .build().compact();

    private final Plugin plugin;

    private final Map<Player, Long> lastLaunchUse = new WeakHashMap<>();
    private final Map<Block, LaunchPlate> plateMap = new HashMap<>();

    private final Path configPath;
    private LaunchPlateConfig config;

    public LaunchPlateManager(Plugin plugin) {
        this.plugin = plugin;
        this.configPath = plugin.getDataFolder().toPath().resolve("config.yml");
    }

    public void reloadConfig() {
        this.config = ConfigurateLoader.yamlLoader().withAllDefaultSerializers().build()
                .loadObject(this.configPath, LaunchPlateConfig.class);
        this.updatePlateMap();
    }

    public void saveConfig() {
        ConfigurateLoader.yamlLoader().withAllDefaultSerializers().build()
                .saveObject(this.configPath, this.config);
    }

    public synchronized void updateConfig(Consumer<LaunchPlateConfig> consumer) {
        consumer.accept(this.config);
        this.updatePlateMap();
        this.saveConfig();
    }

    private void updatePlateMap() {
        synchronized (this.plateMap) {
            this.plateMap.clear();
            for (LaunchPlate plate : this.config.getPlates()) {
                this.plateMap.put(plate.getBlock(), plate);
            }
        }
    }

    public long getLastLaunchUse(Player player) {
        return this.lastLaunchUse.getOrDefault(player, 0L);
    }

    @ApiStatus.Internal
    public void setLastLaunchUse(Player player) {
        this.lastLaunchUse.put(player, System.currentTimeMillis());
    }

    public @Nullable LaunchPlate getLaunchPlate(Block block) {
        synchronized (this.plateMap) {
            return this.plateMap.get(block);
        }
    }

    public Component getPrefix() {
        return PREFIX;
    }

    public LaunchPlateConfig getConfig() {
        return this.config;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
