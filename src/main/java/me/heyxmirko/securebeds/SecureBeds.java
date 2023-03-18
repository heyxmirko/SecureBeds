package me.heyxmirko.securebeds;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class SecureBeds extends JavaPlugin implements Listener {

    private  final Set<Player> recentlyBrokenBlocksPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin has been loaded!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        recentlyBrokenBlocksPlayers.add(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                recentlyBrokenBlocksPlayers.remove(event.getPlayer());
            }
        }.runTaskLater(this, 10L);
    }

    @EventHandler
    public void onBedInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode().equals(GameMode.SURVIVAL)) {
            if(event.getAction().isRightClick() && event.getClickedBlock() != null) {
                Material clickedBlock = event.getClickedBlock().getType();
                if(recentlyBrokenBlocksPlayers.contains(player) && clickedBlock.name().contains("_BED")) {
                    event.setCancelled(true);
                    player.sendActionBar(ChatColor.RED+"You cannot sleep in this bed yet.");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin has been unloaded!");
    }
}
