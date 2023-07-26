import Listeners.Abilities;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import javax.security.auth.login.Configuration;
import java.nio.channels.FileChannel;
import java.time.chrono.JapaneseChronology;
import java.util.List;

public class CryptScoreboard implements Listener {
    private static CryptScoreboard instance = new CryptScoreboard();
    private CryptScoreboard() {
    }
    public static CryptScoreboard getInstance() {
        return instance;
    }
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();
    Objective o = board.registerNewObjective("Scores","dummy", ChatColor.GREEN + "地牢高分榜");
    JavaPlugin plugin;
    FileConfiguration c;

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        c = plugin.getConfig();
    }

    public void registerBoard(){
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(Player onP : Bukkit.getOnlinePlayers()){
            int score = c.getInt("Crypt.Score." + onP.getName(), -1);
            if(score < 0)continue;
            o.getScore(ChatColor.GREEN + onP.getName()).setScore(score);
        }
        for(OfflinePlayer offP : Bukkit.getOfflinePlayers()){
            int score = c.getInt("Crypt.Score." + offP.getName(), -1);
            if(score < 0)continue;
            o.getScore(ChatColor.GREEN + offP.getName()).setScore(score);
        }
    }
    public void refreshBoard(Player p){
        World w = p.getWorld();
        if(w.getName().equals("world")){
            for(Player wp : w.getPlayers()){
                wp.setScoreboard(board);
            }
        }
    }
    public void setPlayerScore(Player p,int score){
        plugin.getConfig().set("Crypt.Score." + p.getName(),score);
        plugin.saveConfig();
        plugin.reloadConfig();
        o.getScore(ChatColor.GREEN + p.getName()).setScore(score);
        refreshBoard(p);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerTeleport(PlayerTeleportEvent teleportEvent) {
        Player p = teleportEvent.getPlayer();
        Location to = teleportEvent.getTo();
        World toW = to.getWorld();
        if (teleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND ||
                teleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            if (toW.getName().equals("world")) {
                refreshBoard(p);
            } else {
                p.setScoreboard(manager.getNewScoreboard());
            }
        } else {
            p.setScoreboard(manager.getNewScoreboard());
        }
    }
}
