package Commands;

import Generator.Rooms;
import Pool.MobPool;
import Pool.WeaponPool;
import UniversalMethod.Tools;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.Random;

public class CryptMob implements CommandExecutor {
    JavaPlugin plugin;
    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    MobPool mp = MobPool.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player p =(Player) commandSender;
            Location loc = p.getLocation();
            if(p.isOp()){
                if(strings.length == 0){
                    p.sendMessage(ChatColor.RED + "请输入怪物ID");
                    return false;
                }
                try {
                    int type = Integer.parseInt(strings[0]);
                    mp.spawnMiniBossType(loc,type,1,p);
                }catch (Exception e){
                    p.sendMessage(ChatColor.RED + "Oops,看起来你输入的不是数字");
                }
            }else {
                p.sendMessage(ChatColor.RED + "你不是服务器的管理员，所以这个指令你不能用");
            }
        }
        return true;
    }
}
