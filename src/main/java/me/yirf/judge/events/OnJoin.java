package me.yirf.judge.events;

import me.yirf.judge.Judge;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.kyori.adventure.text.Component.text;

public class OnJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("judge.reload")) {
            if (Judge.oldVersion) {
                player.sendMessage(text("An update is available for judge!", NamedTextColor.GREEN));
            }
        }
    }
}

