import Generator.Rooms;
import Listeners.Abilities;
import Pool.MaterialPool;
import Pool.MobPool;
import Pool.UniversalUi;
import Pool.WeaponPool;
import UniversalMethod.Tools;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class CryptEvent implements Listener {
    JavaPlugin plugin;
    Random random = new Random();
    Rooms room = Rooms.getInstance();
    public static HashMap<String, Integer> playerElite = new HashMap<>();
    public static HashMap<String, Integer> playerWave = new HashMap<>();
    public static HashMap<String, Double> playerAmplifier = new HashMap<>();
    public static HashMap<String, MenuStatus> playerMenuStatus = new HashMap<>();
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    HashMap<String, BossBar> playerBossBar = new HashMap<>();
    HashMap<String, BukkitRunnable> playerTask = new HashMap<>();
    HashMap<String, BukkitRunnable> playerCoolDown = new HashMap<>();
    HashMap<String, Inventory> playerMenu = new HashMap<>();
    HashMap<String, ItemStack[]> playerItems = new HashMap<>();
    HashMap<String, float[]> playerExp = new HashMap<>();
    HashMap<String, Integer> playerGigaChad = new HashMap<>();
    HashMap<String, Integer> playerPage = new HashMap<>();
    HashMap<String, Integer> playerUpgradeCounter = new HashMap<>();
    HashMap<String, Integer> playerCryptStatus = new HashMap<>();
    HashMap<String, Integer> playerDifficulty = new HashMap<>();
    HashMap<Integer,Player> cryptPlayerStatus = new HashMap<>();
    HashMap<String, Double> playerDamage = new HashMap<>();
    HashMap<String, Double> playerArmor = new HashMap<>();
    HashMap<String, Double> playerHealth = new HashMap<>();
    HashMap<String, Double> playerAttackSpeed = new HashMap<>();
    HashMap<String, Double> playerSpeed = new HashMap<>();
    HashMap<String, Scoreboard> playerBoard = new HashMap<>();
    HashMap<String,Integer[]>playerStartWeapon = new HashMap<>();
    HashSet<Integer> spawnMobStatus = new HashSet<>();
    HashSet<Integer> roomStatus = new HashSet<>();
    HashSet<String> upgradeStatus = new HashSet<>();
    HashSet<String> speedRunStatus = new HashSet<>();
    HashSet<String> shuffleStatus = new HashSet<>();
    Tools t = Tools.getInstance();
    WeaponPool wp = WeaponPool.getInstance();
    MobPool mp = MobPool.getInstance();
    UniversalUi ui = UniversalUi.getInstance();
    MaterialPool materialPool = MaterialPool.getInstance();
    Abilities abilities = Abilities.getInstance();

    public enum MenuStatus {
        NOT_MENU,
        TELEPORT_MENU,
        UPGRADE_STAT_MENU,
        UPGRADE_STAT_AND_ELITE,
        UPGRADE_MENU,
        UPGRADED,
        WEAPON_MENU,
        WEAPON_KNOWLEDGE_MENU,
        OVER_MENU,
        BOSS_COMPLETE,
        ELITE_MENU,
    }
    public Integer[]defaultWeapon = {0,1,2,3,5,38};

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerDamage(EntityDamageByEntityEvent damageEvent) {
        Player attacker;
        if (damageEvent.getDamager() instanceof Player) {
            attacker = (Player) damageEvent.getDamager();
        } else if (damageEvent.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) damageEvent.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            } else return;
        } else return;
        if (attacker == damageEvent.getEntity()) {
            return;
        }
        World w = attacker.getWorld();
        if(damageEvent.getEntity() instanceof LivingEntity) {
            LivingEntity damaged = (LivingEntity) damageEvent.getEntity();
            String name = attacker.getName();
            int elite = playerElite.getOrDefault(name,-1);
            double recordDamage = playerDamage.getOrDefault(name, 0D);
            double damage = damageEvent.getDamage();
            if(elite == 2){
                if(attacker.getEquipment().getItemInMainHand().getType() == Material.AIR){
                    switch (damaged.getName()){
                        case "§e金胡萝卜神的化身":
                        case "§d巫妖王":
                        case "§6蜂巢僵尸母体":
                        case "§bDJ纯一郎":
                        case "§c“膜术师”高资":
                        case "§c类§6星体":
                            break;
                        default:
                            if(random.nextInt( 3) == 0){
                                ItemStack handItem = damaged.getEquipment().getItemInMainHand();
                                if(handItem.getType() != Material.AIR){
                                    attacker.getEquipment().setItemInMainHand(handItem);
                                    damaged.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                                }
                            }
                    }
                }else if(attacker.getInventory().getItemInMainHand().getType() != Material.CHORUS_FRUIT){
                    attacker.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                    w.playSound(attacker.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
                    w.playSound(attacker.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
                    w.playSound(attacker.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
                    damage *= 2;
                }
            }
            ItemStack hand = attacker.getEquipment().getItemInMainHand();
            if (hand.getType() == Material.AIR) return;
            String tag = t.getLore(hand);
            if(tag.equals(ChatColor.GRAY + "IceShield")){
                if(damageEvent.getEntity().getFreezeTicks() > 0){
                    damage *= 2;
                    damaged.setFreezeTicks(0);
                    w.playSound(damaged.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                    w.playSound(damaged.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                    w.playSound(damaged.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                }else {
                    damaged.setFreezeTicks(200);
                }
            }
            if(tag.equals(ChatColor.GRAY + "SmokeSword")){
                ArrayList<Location>loc = abilities.getSmokeLocations();
                boolean doubleDamage = false;
                for (Location l : loc){
                    if(t.distance(l,damaged.getLocation()) < 5){
                        doubleDamage = true;
                    }
                }
                if(doubleDamage) {
                    damage *= 2;
                    w.playSound(damaged.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    w.playSound(damaged.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    w.playSound(damaged.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                }
            }
            if(tag.equals(ChatColor.GRAY + "MeleeBow")){
                double distance = t.distance(damaged.getLocation(),attacker.getLocation());
                if(damageEvent.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    if (distance < 4) {
                        damage *= 1.1;
                        abilities.explosion(attacker, damaged.getEyeLocation(), 10, 3, 3, false, null);
                    }
                }
            }
            damageEvent.setDamage(damage + recordDamage);
        }
    }
    @EventHandler
    public void playerItemChange(PlayerItemHeldEvent heldEvent) {
        Player p = heldEvent.getPlayer();
        Inventory inventory = p.getInventory();
        int slot = heldEvent.getNewSlot();
        ItemStack item = inventory.getItem(slot);
        if (item == null) return;
        if (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW || item.getType() == Material.ENCHANTED_BOOK) {
            double recordedSpeed = playerSpeed.getOrDefault(p.getName(), 0.1D);
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(recordedSpeed);
        } else {
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        }
    }

    @EventHandler
    public void playerSwapItem(PlayerSwapHandItemsEvent swapEvent) {
        Player p = swapEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        ItemStack item = swapEvent.getMainHandItem();
        if (item == null) return;
        if (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW || item.getType() == Material.ENCHANTED_BOOK) {
            double recordedSpeed = playerSpeed.getOrDefault(p.getName(), 0.1D);
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(recordedSpeed);
        } else {
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        }
    }

    @EventHandler
    public void playerLevelUp(PlayerLevelChangeEvent levelChangeEvent) {
        Player p = levelChangeEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        int newLevel = levelChangeEvent.getNewLevel();
        int oldLevel = levelChangeEvent.getOldLevel();
        if (newLevel - oldLevel > 0) {
            if (newLevel % 3 == 0 && newLevel >= 6 || newLevel >= 20) {
                upgradeStatus.add(p.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent teleportEvent) {
        Player p = teleportEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        Location fromLoc = teleportEvent.getFrom();
        PlayerTeleportEvent.TeleportCause cause = teleportEvent.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            teleportEvent.setCancelled(true);
            int y = fromLoc.getBlockY();
            if (y >= 60) {
                p.teleport(fromLoc.add(0, 0, 2));
                openTeleportInv(p);
            }
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent clickEvent) {
        Player p = (Player) clickEvent.getWhoClicked();
        World w = p.getWorld();
        String name = p.getName();
        MenuStatus status = playerMenuStatus.getOrDefault(name, MenuStatus.NOT_MENU);
        int slot = clickEvent.getRawSlot();
        ItemStack item = clickEvent.getCurrentItem();
        if (item == null) return;
        if (status == MenuStatus.TELEPORT_MENU) {
            clickEvent.setCancelled(true);
            p.closeInventory();
            if (slot < 8) {
                playerDifficulty.put(p.getName(), slot / 2);
                float[] exp = new float[]{p.getLevel(), p.getExp()};
                playerExp.put(p.getName(), exp);
                playerItems.put(p.getName(), p.getInventory().getContents());
                startItem(p);
                teleportPlayer(p);
            }
        }
        if (status == MenuStatus.UPGRADE_MENU) {
            clickEvent.setCancelled(true);
            Scoreboard board = playerBoard.getOrDefault(p.getName(), null);
            if (board == null) return;
            if (board.getObjective("stats") == null) return;
            Objective stats = board.getObjective("stats");
            if (slot == 8) {
                p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
                playerMenuStatus.put(name, MenuStatus.UPGRADED);
                p.closeInventory();
                int gigaChadNum = playerGigaChad.getOrDefault(p.getName(), 0);
                gigaChadNum += 1;
                playerGigaChad.put(p.getName(), gigaChadNum);
                Team gigaChad;
                if (board.getTeam("GigaChad") == null) {
                    gigaChad = board.registerNewTeam("GigaChad");
                } else {
                    gigaChad = board.getTeam("GigaChad");
                }
                gigaChad.addEntry(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:");
                gigaChad.setSuffix(" " + gigaChadNum);
                stats.getScore(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:").setScore(-7);
            } else if (slot < 8) {
                p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
                t.removeName(item);
                Item drop = w.dropItem(p.getLocation(), item);
                drop.setPickupDelay(0);
                playerMenuStatus.put(name, MenuStatus.UPGRADED);
                p.closeInventory();
            }
        }
        if (status == MenuStatus.UPGRADE_STAT_MENU) {
            clickEvent.setCancelled(true);
            Scoreboard board = playerBoard.getOrDefault(p.getName(), null);
            if (board == null) return;
            if (board.getObjective("stats") == null) return;
            Objective stats = board.getObjective("stats");
            if (slot < 9) {
                p.playSound(p.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
            }
            switch (slot) {
                case 0:
                    double damage = playerDamage.getOrDefault(name, 0D);
                    damage += 2;
                    playerDamage.put(name, damage);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team attackDamage;
                    if (board.getTeam("AttackDamage") == null) {
                        attackDamage = board.registerNewTeam("AttackDamage");
                    } else {
                        attackDamage = board.getTeam("AttackDamage");
                    }
                    attackDamage.addEntry(ChatColor.BOLD + "攻击力:");
                    attackDamage.setSuffix(" + " + damage);
                    stats.getScore(ChatColor.BOLD + "攻击力:").setScore(-4);
                    break;
                case 2:
                    double attackSpeed = playerAttackSpeed.getOrDefault(p.getName(), 4D);
                    attackSpeed += 0.2;
                    p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
                    playerAttackSpeed.put(p.getName(), attackSpeed);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team attackSpeedStat;
                    if (board.getTeam("AttackSpeed") == null) {
                        attackSpeedStat = board.registerNewTeam("AttackSpeed");
                    } else {
                        attackSpeedStat = board.getTeam("AttackSpeed");
                    }
                    attackSpeedStat.addEntry(ChatColor.YELLOW + "攻击速度:");
                    attackSpeedStat.setSuffix(" + " + String.format("%.1f", attackSpeed - 4));
                    stats.getScore(ChatColor.YELLOW + "攻击速度:").setScore(-5);
                    break;
                case 4:
                    double armor = playerArmor.getOrDefault(p.getName(), 8D);
                    double health = playerHealth.getOrDefault(p.getName(),20D);
                    if(armor >= 20){
                        health += 2;
                        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                        playerHealth.put(p.getName(),health);
                    }else {
                        armor += 2;
                        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
                        playerArmor.put(p.getName(), armor);
                    }
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    break;
                case 6:
                    double speed = playerSpeed.getOrDefault(name, 0.1);
                    speed += 0.01;
                    playerSpeed.put(name, speed);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team moveSpeed;
                    if (board.getTeam("MoveSpeed") == null) {
                        moveSpeed = board.registerNewTeam("MoveSpeed");
                    } else {
                        moveSpeed = board.getTeam("MoveSpeed");
                    }
                    double displaySpeed = (speed - 0.1) * 10 / 0.01;
                    moveSpeed.addEntry(ChatColor.AQUA + "远程武器移动速度:");
                    moveSpeed.setSuffix(" + " + String.format("%.0f", displaySpeed) + "%");
                    stats.getScore(ChatColor.AQUA + "远程武器移动速度:").setScore(-6);
                    break;
                case 8:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    int gigaChadNum = playerGigaChad.getOrDefault(p.getName(), 0);
                    gigaChadNum += 1;
                    playerGigaChad.put(p.getName(), gigaChadNum);
                    Team gigaChad;
                    if (board.getTeam("GigaChad") == null) {
                        gigaChad = board.registerNewTeam("GigaChad");
                    } else {
                        gigaChad = board.getTeam("GigaChad");
                    }
                    gigaChad.addEntry(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:");
                    gigaChad.setSuffix(" " + gigaChadNum);
                    stats.getScore(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:").setScore(-7);
            }
        }
        if (status == MenuStatus.UPGRADE_STAT_AND_ELITE) {
            clickEvent.setCancelled(true);
            Scoreboard board = playerBoard.getOrDefault(p.getName(), null);
            if (board == null) return;
            if (board.getObjective("stats") == null) return;
            Objective stats = board.getObjective("stats");
            if (slot < 9) {
                p.playSound(p.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
            }
            switch (slot) {
                case 0:
                    double damage = playerDamage.getOrDefault(name, 0D);
                    damage += 2;
                    playerDamage.put(name, damage);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team attackDamage;
                    if (board.getTeam("AttackDamage") == null) {
                        attackDamage = board.registerNewTeam("AttackDamage");
                    } else {
                        attackDamage = board.getTeam("AttackDamage");
                    }
                    attackDamage.addEntry(ChatColor.BOLD + "攻击力:");
                    attackDamage.setSuffix(" + " + damage);
                    stats.getScore(ChatColor.BOLD + "攻击力:").setScore(-4);
                    break;
                case 1:
                    double attackSpeed = playerAttackSpeed.getOrDefault(p.getName(), 4D);
                    attackSpeed += 0.2;
                    p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
                    playerAttackSpeed.put(p.getName(), attackSpeed);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team attackSpeedStat;
                    if (board.getTeam("AttackSpeed") == null) {
                        attackSpeedStat = board.registerNewTeam("AttackSpeed");
                    } else {
                        attackSpeedStat = board.getTeam("AttackSpeed");
                    }
                    attackSpeedStat.addEntry(ChatColor.YELLOW + "攻击速度:");
                    attackSpeedStat.setSuffix(" + " + String.format("%.1f", attackSpeed - 4));
                    stats.getScore(ChatColor.YELLOW + "攻击速度:").setScore(-5);
                    break;
                case 2:
                    double armor = playerArmor.getOrDefault(p.getName(), 8D);
                    double health = playerHealth.getOrDefault(p.getName(),20D);
                    if(armor >= 20){
                        health += 2;
                        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                        playerHealth.put(p.getName(),health);
                    }else {
                        armor += 2;
                        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
                        playerArmor.put(p.getName(), armor);
                    }
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    break;
                case 3:
                    double speed = playerSpeed.getOrDefault(name, 0.1);
                    speed += 0.01;
                    playerSpeed.put(name, speed);
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    Team moveSpeed;
                    if (board.getTeam("MoveSpeed") == null) {
                        moveSpeed = board.registerNewTeam("MoveSpeed");
                    } else {
                        moveSpeed = board.getTeam("MoveSpeed");
                    }
                    double displaySpeed = (speed - 0.1) * 10 / 0.01;
                    moveSpeed.addEntry(ChatColor.AQUA + "远程武器移动速度:");
                    moveSpeed.setSuffix(" + " + String.format("%.0f", displaySpeed) + "%");
                    stats.getScore(ChatColor.AQUA + "远程武器移动速度:").setScore(-6);
                    break;
                case 5:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    openEliteMenu(p);
                    break;
                case 8:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    int gigaChadNum = playerGigaChad.getOrDefault(p.getName(), 0);
                    gigaChadNum += 1;
                    playerGigaChad.put(p.getName(), gigaChadNum);
                    Team gigaChad;
                    if (board.getTeam("GigaChad") == null) {
                        gigaChad = board.registerNewTeam("GigaChad");
                    } else {
                        gigaChad = board.getTeam("GigaChad");
                    }
                    gigaChad.addEntry(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:");
                    gigaChad.setSuffix(" " + gigaChadNum);
                    stats.getScore(ChatColor.RED + "" + ChatColor.BOLD + "GigaChad:").setScore(-7);
            }
        }
        if(status == MenuStatus.ELITE_MENU){
            if(slot < 8){
                playerElite.put(p.getName(),slot);
            }
            switch (slot){
                case 0:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    sentryKit(p);
                    break;
                case 1:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    goldKit(p);
                    break;
                case 2:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    gigaKit(p);
                    break;
                case 8:
                    playerMenuStatus.put(name, MenuStatus.UPGRADED);
                    p.closeInventory();
                    openStatsAndEliteInv(p);
            }
        }
        if (status == MenuStatus.WEAPON_MENU) {
            if (slot < 54) {
                clickEvent.setCancelled(true);
                if (slot == 52) {
                    weaponsChangePage(p, true);
                } else if (slot == 53) {
                    weaponsChangePage(p, false);
                } else {
                    if (p.isOp()) {
                        Inventory inv = p.getInventory();
                        ItemStack[] content = inv.getContents();
                        int amount = 0;
                        for (ItemStack itemStack : content) {
                            if (itemStack == null) continue;
                            amount += 1;
                        }
                        if (amount >= 36) {
                            p.sendMessage(ChatColor.RED + "你的背包满了,不能再塞武器了");
                        } else {
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                            inv.addItem(item);
                        }
                    } else {
                        p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                    }
                }
            }
        }
        if (status == MenuStatus.OVER_MENU) {
            Scoreboard board = playerBoard.getOrDefault(p.getName(), null);
            if (board == null) return;
            clickEvent.setCancelled(true);
            if (slot == 2) {
                playerComplete(p, false);
                ItemStack[] recorded = playerItems.getOrDefault(p.getName(), new ItemStack[]{});
                float[] recordedExp = playerExp.getOrDefault(p.getName(), new float[]{0, 0});
                if (recorded.length > 0)
                    p.getInventory().setContents(recorded);
                p.setLevel((int) recordedExp[0]);
                p.setExp(recordedExp[1]);
                playerItems.remove(p.getName());
                playerExp.remove(p.getName());
            } else if (slot == 6) {
                playerWave.put(p.getName(), 41);
                playerAmplifier.put(p.getName(), 5.0);
                p.closeInventory();
                p.setHealth(20);
            }
        }
        if(status == MenuStatus.WEAPON_KNOWLEDGE_MENU){
            clickEvent.setCancelled(true);
            if(slot == 0 || slot == 4){
                playerMenuStatus.put(name, MenuStatus.UPGRADED);
                int x = playerCryptStatus.getOrDefault(name, -1);
                if(x > 0) {
                    spawnMobStatus.add(x);
                    Location roomLoc = new Location(w, x * 50, 10, 0);
                    Location teleportLoc = roomLoc.clone().add(15, 1, 15);
                    cryptMobUpdater(teleportLoc.clone(), p, x);
                }else {
                    p.sendMessage(ChatColor.RED + "地牢加载出错了!一定不是服务器的问题,一定不是!");
                }
                p.closeInventory();
            }else if(slot == 8){
                playerMenuStatus.put(name, MenuStatus.UPGRADED);
                p.setHealth(0);
            }
        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent closeEvent) {
        Player p = (Player) closeEvent.getPlayer();
        Inventory inv = p.getInventory();
        inv.remove(ui.attackDamage());
        inv.remove(ui.attackSpeed());
        inv.remove(ui.armor());
        inv.remove(ui.movementSpeed());
        inv.remove(ui.close());
        String name = p.getName();
        MenuStatus status = playerMenuStatus.getOrDefault(name, MenuStatus.NOT_MENU);
        switch (status) {
            case ELITE_MENU:
            case UPGRADE_MENU:
            case UPGRADE_STAT_MENU:
            case WEAPON_KNOWLEDGE_MENU:
            case UPGRADE_STAT_AND_ELITE:
                Inventory fakeInv = Bukkit.createInventory(p, 9, ChatColor.BLUE + "如果你看到这个证明是BUG，请联系腐竹");
                Inventory saved = playerMenu.getOrDefault(name, fakeInv);
                BukkitRunnable open = new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.openInventory(saved);
                    }
                };
                open.runTaskLater(plugin, 5L);
                playerMenuStatus.put(name, status);
                return;
        }
        playerMenuStatus.put(name, MenuStatus.NOT_MENU);
        playerPage.remove(name);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent deathEvent) {
        Player p = deathEvent.getEntity();
        World w = p.getWorld();
        playerMenuStatus.put(p.getName(), MenuStatus.UPGRADED);
        if (!(w.getName().equals("world"))) return;
        List<Entity> entities = p.getNearbyEntities(30, 30, 30);
        for (Entity e : entities) {
            if (e instanceof Player) continue;
            e.remove();
        }
        if (p.getLocation().getY() < 50) {
            playerComplete(p, true);
        }
        if (playerBossBar.getOrDefault(p.getName(), null) != null) {
            BossBar bar = playerBossBar.get(p.getName());
            bar.removeAll();
        }
    }
    @EventHandler
    public void playerRespawn(PlayerRespawnEvent respawnEvent){
        Player p = respawnEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        BukkitRunnable returnItems = new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] recorded = playerItems.getOrDefault(p.getName(), new ItemStack[]{});
                float[] recordedExp = playerExp.getOrDefault(p.getName(), new float[]{0, 0});
                if (recorded.length > 0)
                    p.getInventory().setContents(recorded);
                p.setLevel((int) recordedExp[0]);
                p.setExp(recordedExp[1]);
                playerItems.remove(p.getName());
                playerExp.remove(p.getName());
                CryptScoreboard.getInstance().refreshBoard(p);
            }
        };
        returnItems.runTaskLater(plugin,20L);
    }
    @EventHandler
    public void playerCraft(EntityPickupItemEvent pickupItemEvent) {
        if (!(pickupItemEvent.getEntity() instanceof Player)) return;
        Player player = (Player) pickupItemEvent.getEntity();
        Inventory inv = player.getInventory();
        World w = player.getWorld();
        if (!(w.getName().equals("world"))) return;
        Item pickedItem = pickupItemEvent.getItem();
        ItemStack pickedItemStack = pickupItemEvent.getItem().getItemStack();
        if (pickedItemStack.getAmount() >= 2) {
            if (pickedItemStack.getType() == Material.BLAZE_POWDER) {
                int amount = pickedItemStack.getAmount();
                pickedItemStack.setAmount(amount - 2);
                if (amount - 2 == 0) {
                    pickedItem.remove();
                } else {
                    pickedItem.setItemStack(pickedItemStack);
                }
                Item meteor = w.dropItem(player.getLocation(), wp.meteor());
                meteor.setPickupDelay(0);
                pickupItemEvent.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                return;
            }
        }
        if (pickedItemStack.getType() == Material.COOKED_CHICKEN) {
            ItemMeta meta = pickedItemStack.getItemMeta();
            int amount = pickedItemStack.getAmount();
            if (meta.getDisplayName().equals(ChatColor.GOLD + "数一数二的烧鸡")) {
                abilities.eat(player, 4 * amount);
                player.sendTitle(" ", ChatColor.GOLD + "你被烧鸡烧到了,移动速度降低了", 10, 30, 10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100 * amount, 1));
            } else {
                abilities.eat(player, 12 * amount);
            }
            pickupItemEvent.setCancelled(true);
            pickupItemEvent.getItem().remove();
            return;
        }
        if (pickedItemStack.getType() == Material.DRIED_KELP) {
            int amount = pickedItemStack.getAmount();
            abilities.eat(player, amount * 5);
            pickupItemEvent.setCancelled(true);
            pickupItemEvent.getItem().remove();
            return;
        }
        if (pickedItemStack.getType() == Material.BREAD) {
            int amount = pickedItemStack.getAmount();
            abilities.eat(player, amount * 8);
            pickupItemEvent.setCancelled(true);
            pickupItemEvent.getItem().remove();
            return;
        }
        ItemStack[] items = inv.getContents();
        boolean crafted = false;
        for (ItemStack item : items) {
            if (item == null) continue;
            ItemMeta itemMeta = item.getItemMeta();
            ArrayList<Integer> formulaID = wp.craftItem(pickedItemStack);
            if (formulaID.size() > 0) {
                for (int i : formulaID) {
                    ItemStack craftedItem = wp.getWeapons()[i];
                    String craftedName = craftedItem.getItemMeta().getDisplayName();
                    if (itemMeta.getDisplayName().equals("")) continue;
                    String itemName = itemMeta.getDisplayName();
                    if (itemName.equals(craftedName)) {
                        item.setAmount(0);
                        crafted = true;
                        break;
                    }
                }
            }
        }
        if (crafted) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            if (!inv.contains(Material.ARROW)) {
                Item arrow = w.dropItem(player.getLocation(), new ItemStack(Material.ARROW));
                arrow.setPickupDelay(0);
            }
        }
    }

    @EventHandler
    public void playerRightClick(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        int wave = playerWave.getOrDefault(p.getName(), -1);
        if (p.getLocation().getY() < 50) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = interactEvent.getClickedBlock();
                if (block.getType() == Material.CHEST) {
                    interactEvent.setCancelled(true);
                    block.setType(Material.AIR);
                    w.playSound(block.getLocation(), Sound.BLOCK_CHEST_OPEN, 2, 1);
                    if (wave == 41) {
                        openOverMenu(p);
                    } else if (wave > 80) {
                        playerComplete(p, false);
                        ItemStack[] recorded = playerItems.getOrDefault(p.getName(), new ItemStack[]{});
                        float[] recordedExp = playerExp.getOrDefault(p.getName(), new float[]{0, 0});
                        if (recorded.length > 0)
                            p.getInventory().setContents(recorded);
                        p.setLevel((int) recordedExp[0]);
                        p.setExp(recordedExp[1]);
                        playerItems.remove(p.getName());
                        playerExp.remove(p.getName());
                    } else {
                        if(random.nextInt(10) == 0)
                            openStatsAndEliteInv(p);
                        else
                            openStatsInv(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerCommand(PlayerCommandPreprocessEvent commandEvent) {
        Player p = commandEvent.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().equals("world"))) return;
        if(p.isOp())return;
        if (playerCryptStatus.getOrDefault(p.getName(), -1) != -1) {
            commandEvent.setCancelled(true);
            p.sendTitle(ChatColor.RED + "Oops", ChatColor.RED + "地牢里不能使用指令", 10, 30, 10);
        }
    }

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent breakEvent){
        Player p = breakEvent.getPlayer();
        if(playerCryptStatus.getOrDefault(p,-1) != -1){
            breakEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void mobDeath(EntityDeathEvent deathEvent) {
        Entity dead = deathEvent.getEntity();
        World w = dead.getWorld();
        int x = dead.getLocation().getBlockX() / 50;
        Location roomLoc = new Location(w, x * 50, 10, 0);
        Location cryptLoc = roomLoc.clone().add(15, 1, 15);
        Player p = cryptPlayerStatus.getOrDefault(x,null);
        BukkitRunnable detect = new BukkitRunnable() {
            @Override
            public void run() {
                Collection<Entity> entities = w.getNearbyEntities(cryptLoc, 23, 23, 23);
                int count = 0;
                for (Entity e : entities) {
                    if (e instanceof Monster) {
                        if(e.isDead())continue;
                        count += 1;
                        if(p != null){
                            ((Monster) e).setTarget(p);
                        }
                    }
                    if(e instanceof Vindicator){
                        if(e.isDead())continue;
                        count += 1;
                        if(p != null){
                            ((Vindicator) e).setTarget(p);
                        }
                    }
                }
                if (count == 0) {
                    spawnMobStatus.add(x);
                }
            }
        };
        detect.runTaskLater(plugin,20L);
        if (p != null) {
            if (dead.getCustomName() != null) {
                String name = dead.getCustomName();
                switch (name) {
                    case "§e金胡萝卜神的化身":
                    case "§d巫妖王":
                    case "§6蜂巢僵尸母体":
                    case "§bDJ纯一郎":
                    case "§c“膜术师”高资":
                    case "§c类§6星体":
                        p.giveExp(20);
                        playerMenuStatus.put(p.getName(),MenuStatus.BOSS_COMPLETE);
                        Bukkit.broadcastMessage(ChatColor.AQUA + p.getName() + "刚刚干掉了 " + name + ChatColor.AQUA +
                                ",Wow!");
                        break;
                    default:
                        p.giveExp(12);
                }
            }else {
                p.giveExp(3);
            }
        }
    }
    @EventHandler
    public void entityExplode(EntityExplodeEvent explodeEvent){
        Entity dead = explodeEvent.getEntity();
        World w = dead.getWorld();
        explodeEvent.setCancelled(true);
        w.createExplosion(dead.getLocation(),4f,false,false,dead);
        BukkitRunnable detect = new BukkitRunnable() {
            @Override
            public void run() {
                int x = dead.getLocation().getBlockX() / 50;
                Location roomLoc = new Location(w, x * 50, 10, 0);
                Location cryptLoc = roomLoc.clone().add(15, 1, 15);
                Collection<Entity> entities = w.getNearbyEntities(cryptLoc, 23, 23, 23);
                int count = 0;
                for (Entity e : entities) {
                    if (e instanceof Monster) {
                        if(e.isDead())continue;
                        count += 1;
                    }
                    if(e instanceof Vindicator){
                        if(e.isDead())continue;
                        count += 1;
                    }
                }
                if (count == 0) {
                    spawnMobStatus.add(x);
                }
            }
        };
        detect.runTaskLater(plugin,20L);
    }

    public void waveProgress(Player p, long time) {
        World w = p.getWorld();
        BukkitRunnable task = playerCoolDown.getOrDefault(p.getName(),null);
        if(task != null){
            task.cancel();
        }
        if (playerBoard.getOrDefault(p.getName(), null) == null) return;
        Scoreboard board = playerBoard.getOrDefault(p.getName(), null);
        if(board.getObjective("stats") == null)return;
        Objective stats = board.getObjective("stats");
        Team waveTime;
        if(board.getTeam("WaveProgress") == null){
            waveTime = board.registerNewTeam("WaveProgress");
        }else {
            waveTime = board.getTeam("WaveProgress");
        }
        int wave = playerWave.getOrDefault(p.getName(),1);
        waveTime.addEntry(ChatColor.AQUA + "速杀挑战时间: ");
        stats.getScore(ChatColor.AQUA + "速杀挑战时间: ").setScore(-3);
        if(speedRunStatus.contains(p.getName()) && wave > 1){
            int difficulty = playerDifficulty.getOrDefault(p.getName(),1);
            Item item = w.dropItem(p.getLocation(),wp.chicken());
            int exp = (int) ((wave + 4) * ((difficulty + 1) * 0.5));
            p.giveExp(exp);
            item.setPickupDelay(0);
            String message = ChatColor.AQUA + "你完成了速杀挑战,获得了" + ChatColor.GOLD + "食物"
                    + ChatColor.AQUA + "和" + ChatColor.GREEN + "额外的" + exp + "点经验值";
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,2);
        }
        speedRunStatus.add(p.getName());
        BukkitRunnable coolDown = new BukkitRunnable() {
            int progress = (int) time / 20;

            @Override
            public void run() {
                waveTime.setSuffix(ChatColor.RED + "" + progress);
                if (progress <= 0) {
                    speedRunStatus.remove(p.getName());
                    this.cancel();
                }
                progress -= 1;
            }
        };
        playerCoolDown.put(p.getName(),coolDown);
        coolDown.runTaskTimer(plugin, 0L, 20L);
    }
    public void weaponsChangePage(Player p,boolean pageUp){
        int currentPage = playerPage.getOrDefault(p.getName(),0);
        int maxPage = wp.getWeapons().length / 52;
        ItemStack[]weapons = wp.getWeapons().clone();
        if(pageUp){
            if(currentPage == 0){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54,ChatColor.RED + "武器列表");
                int positiveBound = currentPage * 52;
                int negativeBound = (currentPage - 1) * 52;
                for(int i = negativeBound; i < positiveBound;i ++){
                    if(i >= weapons.length)break;
                    inv.addItem(weapons[i]);
                }
                inv.setItem(52,ui.pageUP());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                currentPage -= 1;
                playerPage.put(p.getName(),currentPage);
            }
        }else {
            if(currentPage + 1 > maxPage){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                currentPage += 1;
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54,ChatColor.RED + "武器列表");
                int positiveBound = (currentPage + 1) * 52;
                int negativeBound = currentPage * 52;
                for(int i = negativeBound; i < positiveBound;i ++){
                    if(i >= weapons.length)break;
                    inv.addItem(weapons[i]);
                }
                inv.setItem(52,ui.pageUP());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                playerPage.put(p.getName(),currentPage);
            }
        }
        playerMenuStatus.put(p.getName(), CryptEvent.MenuStatus.WEAPON_MENU);
    }

    public void startItem(Player p) {
        Inventory inv = p.getInventory();
        inv.clear();
        if(playerStartWeapon.getOrDefault(p.getName(),new Integer[0]).length > 0){
            Integer[] weapon = playerStartWeapon.get(p.getName());
            if(shuffleStatus.contains(p.getName())){
                t.shuffleInt(weapon);
                playerStartWeapon.put(p.getName(),weapon);
                shuffleStatus.remove(p.getName());
            }
            inv.addItem(wp.getWeapons()[weapon[0]]);
        }else {
            Integer[]clone = defaultWeapon.clone();
            t.shuffleInt(clone);
            playerStartWeapon.put(p.getName(),clone);
            inv.addItem(wp.getWeapons()[clone[0]]);
        }
        inv.addItem(new ItemStack(Material.ARROW));
        inv.addItem(ui.suicide());
        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
    }
    public void openTeleportInv(Player p) {
        String name = p.getName();
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.DARK_BLUE + "选择难度");
        inv.setItem(0,ui.easy());
        inv.setItem(2,ui.normal());
        inv.setItem(4,ui.hard());
        inv.setItem(6,ui.veryHard());
        inv.setItem(8, ui.cancel());
        playerMenuStatus.put(name, MenuStatus.TELEPORT_MENU);
        BukkitRunnable open = new BukkitRunnable() {
            @Override
            public void run() {
                p.openInventory(inv);
            }
        };
        open.runTaskLater(plugin, 4L);
    }
    public void openLevelUpInv(Player p) {
        String name = p.getName();
        BukkitRunnable task = playerCoolDown.getOrDefault(p.getName(),null);
        if(task != null){
            task.cancel();
        }
        if(playerCryptStatus.getOrDefault(name,-1) != -1) {
            int amount = 0;
            Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GREEN + "  武器进化<==" + ChatColor.BLACK + " [空格] " + ChatColor.LIGHT_PURPLE + "==>随机武器");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            ItemStack[] items = p.getInventory().getContents();
            int count = 0;
            for (ItemStack item : items) {
                if (item == null) continue;
                ItemStack[] ingredient = wp.breakItem(item);
                if (ingredient.length == 0) continue;
                amount += ingredient.length;
                inv.addItem(ingredient);
                count += 1;
                if(count >= 2)break;
            }
            ItemStack[] rest = wp.randomMaterial(9 - amount + 1);
            for (int i = 0; i < rest.length; i++) {
                if (amount + 1 + i >= 9) break;
                inv.setItem(amount + 1 + i, rest[i]);
            }
            inv.setItem(8, ui.close());
            playerMenuStatus.put(name, MenuStatus.UPGRADE_MENU);
            playerMenu.put(name, inv);
            upgradeStatus.remove(name);
            p.openInventory(inv);
        }
    }
    public void openStatsInv(Player p){
        p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
        BukkitRunnable task = playerCoolDown.getOrDefault(p.getName(),null);
        if(task != null){
            task.cancel();
        }
        String name = p.getName();
        if(playerCryptStatus.getOrDefault(name,-1) != -1) {
            Inventory inv = Bukkit.createInventory(p, 9, ChatColor.YELLOW + "选择属性升级");
            inv.setItem(0, ui.attackDamage());
            inv.setItem(2, ui.attackSpeed());
            inv.setItem(4, ui.armor());
            inv.setItem(6, ui.movementSpeed());
            inv.setItem(8, ui.close());
            playerMenuStatus.put(name, MenuStatus.UPGRADE_STAT_MENU);
            playerMenu.put(name, inv);
            upgradeStatus.remove(name);
            p.openInventory(inv);
        }
    }
    public void openStatsAndEliteInv(Player p){
        p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
        BukkitRunnable task = playerCoolDown.getOrDefault(p.getName(),null);
        if(task != null){
            task.cancel();
        }
        String name = p.getName();
        if(playerCryptStatus.getOrDefault(name,-1) != -1) {
            Inventory inv = Bukkit.createInventory(p, 9, ChatColor.RED + "选择属性升级/选择精英装备");
            inv.setItem(0, ui.attackDamage());
            inv.setItem(1, ui.attackSpeed());
            inv.setItem(2, ui.armor());
            inv.setItem(3, ui.movementSpeed());
            inv.setItem(5, ui.eliteKit());
            inv.setItem(8, ui.close());
            playerMenuStatus.put(name, MenuStatus.UPGRADE_STAT_AND_ELITE);
            playerMenu.put(name, inv);
            upgradeStatus.remove(name);
            p.openInventory(inv);
        }
    }
    public void openEliteMenu(Player p){
        BukkitRunnable task = playerCoolDown.getOrDefault(p.getName(),null);
        if(task != null){
            task.cancel();
        }
        String name = p.getName();
        if(playerCryptStatus.getOrDefault(name,-1) != -1) {
            Inventory inv = Bukkit.createInventory(p, 9, ChatColor.RED + "选择精英装备");
            inv.setItem(0, ui.sentryKit());
            inv.setItem(1, ui.goldenCarrotKit());
            inv.setItem(2, ui.gigaChadKit());
            inv.setItem(8, ui.closeElite());
            playerMenuStatus.put(name, MenuStatus.ELITE_MENU);
            playerMenu.put(name, inv);
            upgradeStatus.remove(name);
            p.openInventory(inv);
        }
    }
    public void openOverMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9,ChatColor.DARK_BLUE + "选择你的道路");
        inv.setItem(2,ui.deathMask());
        inv.setItem(6,ui.liveMask());
        p.openInventory(inv);
        playerMenuStatus.put(p.getName(),MenuStatus.OVER_MENU);
    }
    public void openWeaponMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9,ChatColor.DARK_BLUE + "了解你的武器");
        Integer[]weapons = playerStartWeapon.getOrDefault(p.getName(),defaultWeapon);
        inv.setItem(4,wp.getWeapons()[weapons[0]]);
        inv.setItem(0,ui.ok());
        inv.setItem(8,ui.no());
        playerMenu.put(p.getName(), inv);
        playerMenuStatus.put(p.getName(),MenuStatus.WEAPON_KNOWLEDGE_MENU);
        p.openInventory(inv);
    }
    public void playerComplete(Player p,boolean dead){
        World w = p.getWorld();
        int wave = playerWave.getOrDefault(p.getName(),0);
        if(!dead) {
            p.sendTitle(ChatColor.YELLOW + "Congratulations", ChatColor.YELLOW + "你完成了目前所有的关卡", 10, 40, 10);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        }
        if(wave != 0){
            wave -= 1;
        }
        int point = p.getTotalExperience();
        int difficulty = playerDifficulty.getOrDefault(p.getName(),1);
        int awardAmount = point / 25;
        int betterAward;
        int gigaChadLevel = playerGigaChad.getOrDefault(p.getName(),0);
        int recordedPoint = plugin.getConfig().getInt("Crypt.Score." + p.getName(),-1);
        if(point > recordedPoint && point > 0) {
            CryptScoreboard.getInstance().setPlayerScore(p,point);
        }
        String message2 = "";
        if(wave >= 10){
            shuffleStatus.add(p.getName());
        }
        if(wave >= 10 && wave < 40){
            awardAmount -= difficulty * 2;
            betterAward = difficulty * 2 + 2;
            t.sendGifts(p,materialPool.betterMaterials(betterAward));
            message2 = ChatColor.YELLOW + "并且抽取了"+ betterAward +"次高级物资";
        }
        if(wave >= 40 && wave < 80){
            if(dead && wave == 40){
                awardAmount -= 5;
                betterAward = (int) (8 * (1 + (difficulty * 0.5)));
                t.sendGifts(p,materialPool.betterMaterials(betterAward));
                message2 = ChatColor.YELLOW + "并且抽取了"+ betterAward +"次高级物资";
            }else{
                awardAmount = (int) (awardAmount * 1.2) - 10;
                betterAward = (int) (15 * (1 + (gigaChadLevel / 10.0)) * (1 + (difficulty * 0.5)));
                t.sendGifts(p,materialPool.betterMaterials(betterAward));
                message2 = ChatColor.YELLOW + "并且抽取了"+ betterAward +"次高级物资";
            }
        }else if(wave == 80){
            awardAmount = (int) (awardAmount * 1.5) - 20;
            awardAmount = (int) (awardAmount * 1.5) - 20;
            betterAward = (int) (30 * (1 + (gigaChadLevel / 10.0)) * (1 + (difficulty * 0.5)));
            t.sendGifts(p,materialPool.betterMaterials(betterAward));
            message2 = ChatColor.YELLOW + "并且抽取了"+ betterAward +"次高级物资";
        }
        int finalAward = (int) (awardAmount * (1 + gigaChadLevel / 10.0));
        ItemStack[]awards = materialPool.randomMaterial(finalAward);
        t.sendGifts(p,awards);
        int x = p.getLocation().getBlockX() / 50;
        String message3;
        switch (difficulty) {
            case 0:
                message3 = ChatColor.WHITE + "我还年轻不想死";
                break;
            case 2:
                message3 = ChatColor.LIGHT_PURPLE + "非常暴力";
                break;
            case 3:
                message3 = ChatColor.RED + "噩梦";
                break;
            default:
                message3 = ChatColor.YELLOW + "用力打我";
                break;
        }
        String message = ChatColor.AQUA + p.getName() + "在" + message3 + ChatColor.AQUA + "难度中" +
                "坚持到了第" + ChatColor.YELLOW + wave +
                ChatColor.AQUA + "波怪物,抽取了" + ChatColor.YELLOW + finalAward +
                ChatColor.AQUA + "次普通物资";
        Bukkit.broadcastMessage(message);
        if(!message2.equals(""))
            Bukkit.broadcastMessage(message2);
        if (playerTask.getOrDefault(p.getName(), null) != null) {
            BukkitRunnable task = playerTask.get(p.getName());
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        playerTask.remove(p.getName());
        roomStatus.remove(x);
        cryptPlayerStatus.remove(x);
        playerWave.remove(p.getName());
        playerAmplifier.remove(p.getName());
        playerUpgradeCounter.remove(p.getName());
        playerDamage.remove(p.getName());
        playerSpeed.remove(p.getName());
        playerAttackSpeed.remove(p.getName());
        playerArmor.remove(p.getName());
        playerHealth.remove(p.getName());
        playerGigaChad.remove(p.getName());
        playerCryptStatus.remove(p.getName());
        upgradeStatus.remove(p.getName());
        playerDifficulty.remove(p.getName());
        playerElite.remove(p.getName());
        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        Location roomLoc = new Location(w,x * 50,10,0);
        Location cryptLoc = roomLoc.clone().add(15,1,15);
        Collection<Entity>entities = w.getNearbyEntities(cryptLoc,30,30,30);
        for(Entity e : entities){
            if(e instanceof Player)continue;
            e.remove();
        }
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.closeInventory();
        p.getInventory().clear();
        p.teleport(w.getSpawnLocation());
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setExp(0);
        if(awards.length > 0) {
            ComponentBuilder baseText = (new ComponentBuilder(ChatColor.YELLOW + "金胡萝卜神给你赠送了礼物").color(net.md_5.bungee.api.ChatColor.YELLOW).append(",使用").color(net.md_5.bungee.api.ChatColor.GRAY));
            TextComponent clickableText = new TextComponent("/giftlist");
            clickableText.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            clickableText.setUnderlined(Boolean.TRUE);
            clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("点击即可快速输入指令")).color(net.md_5.bungee.api.ChatColor.GRAY).create()));
            clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/giftlist"));
            baseText.append(clickableText).append("来打开你的礼物箱").color(net.md_5.bungee.api.ChatColor.GRAY).underlined(false);
            p.spigot().sendMessage(baseText.create());
        }
    }
    public void sentryKit(Player p){
        p.getInventory().clear();
        World w = p.getWorld();
        Inventory inv = p.getInventory();
        EntityEquipment equipment = p.getEquipment();
        BukkitRunnable helm = new BukkitRunnable() {
            @Override
            public void run() {
                equipment.setHelmet(wp.sentryHelm());
                w.playSound(p.getEyeLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
                w.playSound(p.getEyeLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            }
        };
        helm.runTaskLater(plugin,20L);
        equipment.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        inv.addItem(wp.VillarPerosa());
        inv.addItem(ui.suicide());
    }
    public void goldKit(Player p){
        p.getInventory().clear();
        Inventory inv = p.getInventory();
        EntityEquipment equipment = p.getEquipment();
        equipment.setHelmet(new ItemStack(Material.GOLDEN_CARROT));
        equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        inv.addItem(wp.goldenZweiHander());
        inv.addItem(ui.suicide());
    }
    public void gigaKit(Player p){
        p.getInventory().clear();
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(21);
        Inventory inv = p.getInventory();
        inv.setItem(1,ui.suicide());
    }

    public void teleportPlayer(Player p){
        World w = p.getWorld();
        int x = -1;
        for(int i = 1;i <= 20;i ++){
            if(!roomStatus.contains(i)){
                x = i;
                break;
            }
        }
        if(x == -1){
            p.sendMessage(ChatColor.RED + "房间满了,请等一会再来吧");
            return;
        }
        roomStatus.add(x);
        Location roomLoc = new Location(w,x * 50,10,0);
        Location teleportLoc = roomLoc.clone().add(15,1,15);
        room.generateRoom(roomLoc.clone(),30,0);
        int finalX = x;
        BukkitRunnable teleport = new BukkitRunnable() {
            @Override
            public void run() {
                p.teleport(teleportLoc.clone());
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,86400,0));
                p.setHealth(20);
                p.setLevel(0);
                p.setExp(0);
                p.setTotalExperience(0);
                p.setFoodLevel(20);
                Scoreboard board = manager.getNewScoreboard();
                Objective stats = board.registerNewObjective("stats","dummy",ChatColor.YELLOW + "玩家数据");
                stats.setDisplaySlot(DisplaySlot.SIDEBAR);
                Team difficulty = board.registerNewTeam("Difficulty");
                difficulty.addEntry(ChatColor.WHITE + "地牢难度:");
                int diff = playerDifficulty.getOrDefault(p.getName(),1);
                switch (diff){
                    case 0:
                        difficulty.setSuffix(ChatColor.WHITE + "我还年轻不想死");
                        break;
                    case 1:
                        difficulty.setSuffix(ChatColor.YELLOW + "用力打我");
                        break;
                    case 2:
                        difficulty.setSuffix(ChatColor.LIGHT_PURPLE + "非常暴力");
                        break;
                    case 3:
                        difficulty.setSuffix(ChatColor.RED + "噩梦");
                        break;
                }
                stats.getScore(ChatColor.WHITE + "地牢难度:").setScore(-1);
                playerBoard.put(p.getName(),board);
                playerCryptStatus.put(p.getName(), finalX);
                cryptPlayerStatus.put(finalX,p);
                p.setScoreboard(board);
                openWeaponMenu(p);
            }
        };
        teleport.runTaskLater(plugin,10L);
    }
    public void cryptMobUpdater(Location cryptLoc,Player owner,int cryptX){
        String ownerName = owner.getName();
        BukkitRunnable updater = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                MenuStatus status = playerMenuStatus.getOrDefault(ownerName,MenuStatus.NOT_MENU);
                int elite = playerElite.getOrDefault(ownerName,-1);
                if (upgradeStatus.contains(ownerName) && spawnMobStatus.contains(cryptX) && status != MenuStatus.BOSS_COMPLETE && status != MenuStatus.OVER_MENU) {
                    int upgrade = playerUpgradeCounter.getOrDefault(ownerName, 0);
                    if (upgrade == 2 && elite == -1) {
                        openLevelUpInv(owner);
                        playerUpgradeCounter.put(ownerName, 0);
                    } else {
                        openStatsInv(owner);
                        playerUpgradeCounter.put(ownerName, upgrade + 1);
                    }
                } else if(spawnMobStatus.contains(cryptX) && status == MenuStatus.NOT_MENU){
                    spawnMobStatus.remove(cryptX);
                    int wave = playerWave.getOrDefault(ownerName, 1);
                    int difficulty = playerDifficulty.getOrDefault(ownerName,1);
                    double amplifier;
                    if(difficulty > 1){
                        amplifier = playerAmplifier.getOrDefault(ownerName,(difficulty - 1) * 0.25);
                    }else {
                        amplifier = playerAmplifier.getOrDefault(ownerName, 0.1);
                    }
                    if(wave > 80){
                        this.cancel();
                        return;
                    }
                    spawnMob(cryptLoc, wave, amplifier, owner);
                }
                if(count >= 10){
                    int wave = playerWave.getOrDefault(ownerName, 1) - 2;
                    int type = wave / 10;
                    double amplifier = playerAmplifier.getOrDefault(ownerName, 0.1);
                    if(type >= 4){
                        type -= 4;
                    }
                    if(random.nextBoolean()){
                        abilities.mapAbility(cryptLoc,owner,type,amplifier);
                    }
                    count = 0;
                }
                count += 1;
            }
        };
        updater.runTaskTimer(plugin,20L,20L);
        playerTask.put(ownerName,updater);
    }
    public void spawnMob(Location loc,int wave,double amplifier,Player target) {
        World w = target.getWorld();
        Scoreboard board = playerBoard.getOrDefault(target.getName(), null);
        if (board == null) return;
        int difficulty = playerDifficulty.getOrDefault(target.getName(),1);
        int playerCurrentWave = wave;
        int amount = 3 + (3 * Math.min(difficulty,2));
        if(wave == 80){
            mp.goldenCarrotForm(loc,Math.min(1,difficulty + 0.5),target);
            amount = 0;
        }
        if (wave % 40 == 0 && amount > 0) {
            mp.starBody(loc, Math.min(1,difficulty + 0.5), target);
            amount = 0;
        }
        if (wave % 2 == 0 && amount > 0) {
            if(difficulty == 0){
                mp.spawnMiniBoss(loc,1,amplifier/1.5,target);
                amount -= 1;
            }else {
                mp.spawnMiniBoss(loc, 2, amplifier / 1.5, target);
                amount -= 2;
            }
        }
        if (wave % 10 == 0 && wave < 80 && amount > 0) {
            mp.spawnBoss(loc,amplifier / 5 + 0.4, target);
            amount -= 4;
        }
        if(wave % 10 == 0 && wave != 0){
            waveProgress(target,1000L);
        }else {
            waveProgress(target,400L);
        }
        if(difficulty == 3){
            mp.spawnMiniBoss(loc,amount,amplifier,target);
        }else {
            mp.spawnMobs(loc, amount, amplifier, target);
        }
        if(board.getObjective("stats") == null)return;
        Objective stats = board.getObjective("stats");
        target.playSound(target.getLocation(),Sound.ENTITY_EVOKER_PREPARE_SUMMON,2,1);
        Team waveNum;
        if(board.getTeam("WaveNumber") == null){
            waveNum = board.registerNewTeam("WaveNumber");
        }else {
            waveNum = board.getTeam("WaveNumber");
        }
        if(wave % 10 == 1){
            Location roomLoc = loc.clone().subtract(15,1,15);
            int type = wave / 10;
            if(type >= 4)
                type -= 4;
            room.generateRoom(roomLoc.clone(),30,type);
            w.strikeLightningEffect(loc);
        }
        String waveCount = ChatColor.RED + "第 " + playerCurrentWave + " 波";
        waveNum.addEntry(ChatColor.RED + "当前波数: ");
        waveNum.setSuffix(ChatColor.RED + "" + playerCurrentWave);
        stats.getScore(ChatColor.RED + "当前波数: ").setScore(-2);
        target.sendTitle(waveCount," ",10,10,10);
        if(difficulty > 0){
            amplifier += 0.1 * difficulty;
        }else {
            amplifier += 0.05;
        }
        playerCurrentWave += 1;
        playerWave.put(target.getName(),playerCurrentWave);
        playerAmplifier.put(target.getName(),amplifier);
    }
}
