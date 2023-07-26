import Pool.UniversalUi;
import Pool.WeaponPool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GetWeapon implements CommandExecutor {
    WeaponPool wp = WeaponPool.getInstance();
    UniversalUi ui = UniversalUi.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            World w = p.getWorld();
            Inventory inv = Bukkit.createInventory(p, 54, ChatColor.RED + "武器列表");
            ItemStack[] weapons = wp.getWeapons();
            for (int i = 0; i < 52; i++) {
                if (i >= weapons.length) break;
                inv.addItem(weapons[i]);
            }
            inv.setItem(52, ui.pageUP());
            inv.setItem(53, ui.pageDown());
            CryptEvent.playerMenuStatus.put(p.getName(), CryptEvent.MenuStatus.WEAPON_MENU);
            p.openInventory(inv);
        }
        return true;
    }
}
