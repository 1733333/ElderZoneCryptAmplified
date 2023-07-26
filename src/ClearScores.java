import Pool.WeaponPool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Set;

public class ClearScores implements CommandExecutor {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    JavaPlugin plugin;

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player sender = ((Player) commandSender);
            if(sender.isOp()){
                Bukkit.broadcastMessage(ChatColor.AQUA + "计分板已清除");
                plugin.getConfig().set("Crypt.Score","");
                plugin.saveConfig();
                plugin.reloadConfig();
                CryptScoreboard.getInstance().registerBoard();
                Server server = sender.getServer();
                World w = server.getWorld("world");
                if(w != null){
                    for(Player p:w.getPlayers()){
                        p.setScoreboard(manager.getNewScoreboard());
                    }
                }
            }
        }
        return true;
    }
}
