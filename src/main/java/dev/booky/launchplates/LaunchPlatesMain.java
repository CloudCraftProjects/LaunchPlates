package dev.booky.launchplates;
// Created by booky10 in CloudCore (10:35 14.03.23)

import dev.booky.cloudcore.util.TranslationLoader;
import dev.booky.launchplates.commands.LaunchPlateCommand;
import dev.booky.launchplates.listener.LaunchListener;
import dev.booky.launchplates.listener.ProtectionListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class LaunchPlatesMain extends JavaPlugin {

    private LaunchPlateManager manager;
    private TranslationLoader i18n;

    @Override
    public void onLoad() {
        new Metrics(this, 18099);
        this.manager = new LaunchPlateManager(this);

        this.i18n = new TranslationLoader(this);
        this.i18n.load();
    }

    @Override
    public void onEnable() {
        this.manager.reloadConfig();

        Bukkit.getPluginManager().registerEvents(new ProtectionListener(this.manager), this);
        Bukkit.getPluginManager().registerEvents(new LaunchListener(this.manager), this);

        LaunchPlateCommand.create(this.manager);

        Bukkit.getServicesManager().register(LaunchPlateManager.class, this.manager, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        if (this.i18n != null) {
            this.i18n.unload();
        }
    }
}
