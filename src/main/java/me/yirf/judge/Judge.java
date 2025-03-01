package me.yirf.judge;

import me.yirf.judge.commands.ReloadCommand;
import me.yirf.judge.config.Config;
import me.yirf.judge.events.OnDisconnect;
import me.yirf.judge.events.OnJoin;
import me.yirf.judge.events.OnSneak;
import me.yirf.judge.events.OnSneakDelay;
import me.yirf.judge.group.Group;
import me.yirf.judge.utils.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public final class Judge extends JavaPlugin {

    File configFile = new File(getDataFolder(), "config.yml");
    FileConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);

    public static Judge instance;
    public static PluginManager pm;
    public static float menuVert;
    public static float menuHoroz;
    public static List<String> menuTexts;
    public static float menuScale;
    public static boolean hasPapi;
    public static List<World> allowedWorlds = new ArrayList<World>();
    public static boolean hasWorldGuard = false;
    public static boolean oldVersion = true;

    @Override
    public void onEnable() {
        instance = this;
        pm = getServer().getPluginManager();
        versionCheck();
        init();
        sched();
        data();

        if (pm.isPluginEnabled("WorldGuard")) {
            hasWorldGuard = true;
        }
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Group.remove(p);
        }
    }

    public void data() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }

        menuVert = Config.getFloat("properties.vertical");
        menuHoroz = Config.getFloat("properties.horizontal");
        menuScale = Config.getFloat("properties.scale");
        menuTexts = Config.getStringList("board");
        hasPapi = Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI");

        if(!Config.getBoolean("allow-all-worlds")) {
            allowedWorlds = Config.getWorldList("allowed-worlds");
        }
    }

    private void versionCheck() {
        String url = "https://api.spiget.org/v2/resources/119624/versions/latest";
        try {
            String jsonResponse = JsonUtil.fetchJSON(url);
            String v = JsonUtil.extractName(jsonResponse);
            if (getDescription().getVersion() == v) {
                oldVersion = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        if(Config.getInt("delay") == 0) {
            pm.registerEvents(new OnSneak(), this);
        } else {
            pm.registerEvents(new OnSneakDelay(), this);
        }
        pm.registerEvents(new OnDisconnect(), this);
        pm.registerEvents(new OnJoin(), this);
        this.getCommand("reloadjudge").setExecutor(new ReloadCommand());

    }

    public void sched() {
        Bukkit.getScheduler().runTaskTimer(this, this::checkFalse, 0L, 20L);
    }

    public void checkFalse() {
        Set<UUID> uuids = Group.group.keySet();
        for (UUID u : uuids) {
            Player player = Bukkit.getPlayer(u);
            if (!player.isSneaking()) {
                Group.remove(player);
            }
        }
    }

    public FileConfiguration getConfigYaml() {
        return configYaml;
    }

    public File getConfigFile() {
        return configFile;
    }
}
