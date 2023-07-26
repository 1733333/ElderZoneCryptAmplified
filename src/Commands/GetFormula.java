package Commands;

import Pool.WeaponPool;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GetFormula implements CommandExecutor {
    WeaponPool wp = WeaponPool.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player sender = ((Player) commandSender);
            String[] formulas = wp.getFormulaOutput();
            if(formulas.length == 0)return false;
            for(int i = 0;i < formulas.length;i ++){
                sender.sendMessage(ChatColor.AQUA + "" + (i + 1) + ": " + formulas[i]);
            }
        }
        return true;
    }
}
