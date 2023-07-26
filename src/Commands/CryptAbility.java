package Commands;

import Listeners.Abilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CryptAbility implements CommandExecutor {
    Abilities ab = Abilities.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player p =(Player) commandSender;
            Location loc = p.getLocation();
            if(p.isOp()){
                if(strings.length == 0){
                    p.sendMessage(ChatColor.RED + "请输入ID");
                    return false;
                }
                try {
                    int type = Integer.parseInt(strings[0]);
                    ab.mapAbility(loc,p,type,1);
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
