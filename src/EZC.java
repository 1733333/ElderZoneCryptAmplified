import Commands.*;
import Generator.Rooms;
import Listeners.Abilities;
import Pool.MobPool;
import Pool.WeaponPool;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EZC extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();
        PlayerListener playerListener = new PlayerListener();
        MonsterListener monsterListener = new MonsterListener();
        CryptMob cryptMob = new CryptMob();
        CryptAbility cryptAbility = new CryptAbility();
        CryptBoss cryptBoss = new CryptBoss();
        GetWeapon getWeapon = new GetWeapon();
        WeaponPool weaponPool = WeaponPool.getInstance();
        Abilities abilities = Abilities.getInstance();
        CryptScoreboard cryptScoreboard = CryptScoreboard.getInstance();
        Rooms rooms = Rooms.getInstance();
        MobPool mobPool = MobPool.getInstance();
        CryptEvent cryptEvent = new CryptEvent();
        NonCryptEvents nonCryptEvents = new NonCryptEvents();
        GetFormula getFormula = new GetFormula();
        ClearScores clearScores = new ClearScores();

        manager.registerEvents(playerListener, this);
        manager.registerEvents(abilities, this);
        manager.registerEvents(monsterListener, this);
        manager.registerEvents(cryptEvent, this);
        manager.registerEvents(nonCryptEvents, this);
        manager.registerEvents(cryptScoreboard,this);

        cryptMob.setPlugin(this);
        abilities.setPlugin(this);
        playerListener.setPlugin(this);
        monsterListener.setPlugin(this);
        mobPool.setPlugin(this);
        cryptEvent.setPlugin(this);
        cryptBoss.setPlugin(this);
        nonCryptEvents.setPlugin(this);
        cryptScoreboard.setPlugin(this);
        clearScores.setPlugin(this);

        weaponPool.registerWeapons();
        weaponPool.registerFormula();
        rooms.registerTextures();
        cryptScoreboard.registerBoard();

        this.getCommand("cryptmob").setExecutor(cryptMob);
        this.getCommand("getweapon").setExecutor(getWeapon);
        this.getCommand("cryptboss").setExecutor(cryptBoss);
        this.getCommand("getformula").setExecutor(getFormula);
        this.getCommand("mapability").setExecutor(cryptAbility);
        this.getCommand("clearscore").setExecutor(clearScores);
    }

    @Override
    public void onDisable() {
        this.saveConfig();
    }
}
