package me.yirf.judge.menu;

import com.viaversion.viaversion.api.Via;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yirf.judge.Judge;
import me.yirf.judge.config.Config;
import me.yirf.judge.group.Group;
import me.yirf.judge.interfaces.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import static org.bukkit.entity.Display.Billboard;

public class Display implements Color {
    public static void spawnMenu(Player player, Player target) {
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion")) {
            if(Via.getAPI().getPlayerVersion(player.getUniqueId()) < 762) {
                Bukkit.broadcastMessage("Less then version!");
                return;
            }
        }
        TextDisplay display = target.getWorld().spawn(target.getLocation(), TextDisplay.class);
        display.setShadowed(true);
        display.setBillboard(Billboard.CENTER);
        display.setVisibleByDefault(true);
        if(!Config.getString("properties.color").equals("DEFAULT")) {
            display.setBackgroundColor(Config.getRGB("properties.color"));
        }


        if (display == null) {
            Judge.instance.getLogger().severe("Unable to spawn display entity");
        }

        display.text(getShow(player, target));

        target.addPassenger(display);
        display.setTransformation(
                new Transformation(
                        new Vector3f(Judge.menuHoroz, Judge.menuVert, 0),
                        new AxisAngle4f(),
                        new Vector3f(Judge.menuScale),
                        new AxisAngle4f()
                )
        );

        player.showEntity(Judge.instance, display);
        Group.add(display, player);
    }

    public static TextComponent getShow(Player player, Player target) {
        TextComponent.Builder text = Component.text();

        for (String line : Judge.menuTexts) {
            if (Judge.hasPapi) {
                line = PlaceholderAPI.setPlaceholders(target, line);
            }

            // Convert color codes before creating the component
            line = Color.format(line)
                    .replaceAll("%player%", target.getName())
                    .replaceAll("%viewer%", player.getName());

            // Convert legacy color codes (§) to Adventure's Component
            TextComponent textComponent = LegacyComponentSerializer.legacySection().deserialize(line);

            text.append(textComponent).append(Component.newline());
        }

        return text.build();
    }
}
