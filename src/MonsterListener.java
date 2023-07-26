import Listeners.Abilities;
import Pool.WeaponPool;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Random;


public class MonsterListener implements Listener {
    JavaPlugin plugin;
    Random random = new Random();
    Abilities ability = Abilities.getInstance();
    WeaponPool weaponPool = WeaponPool.getInstance();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void beeAttack(EntityDamageByEntityEvent damage) {
        Entity entity = damage.getDamager();
        if (entity.getType() == EntityType.BEE) {
            ((Bee)entity).setHealth(0);
        }
    }
    @EventHandler
    public void mobDeath(EntityDeathEvent deathEvent) {
        LivingEntity dead = deathEvent.getEntity();
        World w = dead.getWorld();
        if(!(w.getName().equals("world")))return;
        String name = dead.getName();
        deathEvent.getDrops().clear();
        deathEvent.setDroppedExp(0);
        if(random.nextBoolean()){
            w.dropItem(dead.getLocation(),weaponPool.kelp());
        }
        if (dead.getCustomName() != null) {
            w.dropItem(dead.getLocation(),weaponPool.bread());
        }
        switch (name) {
            case "§6火爆浪子":
                explodeLater(dead);
                break;
            case "§7要塞喷火兵":
                explodeFireLater(dead);
                break;
            case "§f幽灵骷髅":
            case "§7寄生僵尸本体":
            case "§7隐身坤":
                Entity vehicle = dead.getVehicle();
                if (vehicle != null) {
                    if(vehicle.getVehicle() != null){
                        Entity vehicleV = vehicle.getVehicle();
                        if(vehicleV instanceof LivingEntity){
                            ((LivingEntity) vehicleV).setHealth(0);
                        }
                    }
                    if(vehicle instanceof LivingEntity){
                        ((LivingEntity) vehicle).setHealth(0);
                    }
                }
                break;
            case "§a史莱姆僵尸":
                shootManyBall(dead);
                break;
            case "§a牛头人":
                List<Entity>passengers = dead.getPassengers();
                if(passengers.size() == 0)return;
                for (Entity e : passengers){
                    if(e instanceof LivingEntity){
                        ((LivingEntity) e).setHealth(0);
                    }
                }
                break;
            case "§f被箭射成刺猬的僵尸":
                shootManyArrow(dead);
                break;
            case "§e芝士雪豹":
                Bukkit.broadcastMessage(dead.getCustomName() + ChatColor.RED + "闭嘴了");
                break;
            case "§e蜂巢僵尸":
                List<Entity> nearby2 = dead.getNearbyEntities(30, 30, 30);
                for (Entity e : nearby2) {
                    if(e instanceof Bee){
                        ((Bee) e).setHealth(0);
                    }
                }
            case "§d巫妖王的随从":
                List<Entity> nearby = dead.getNearbyEntities(30, 30, 30);
                for (Entity e : nearby) {
                    if (e instanceof LivingEntity) {
                        if (e instanceof Player) {
                            double health = ((Player) e).getHealth();
                            double maxHealth = ((Player) e).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                            health += 6;
                            ((Player) e).setHealth(Math.min(health, maxHealth));
                        }
                        if (e.getName().equals("§d巫妖王")) {
                            ((LivingEntity) e).damage(7);
                        }
                        if (e.getName().equals("§6蜂巢僵尸母体")) {
                            ((LivingEntity) e).damage(7);
                        }
                    }
                }
                break;
            case "§c“膜术师”高资":
                List<Entity>nearby1 = dead.getNearbyEntities(30,30,30);
                for(Entity e : nearby1) {
                    if (e.getName().equals(ChatColor.AQUA + "“膜术师”高资 但是分身")) {
                        e.remove();
                    }
                }
            case "§d巫妖王":
            case "§6蜂巢僵尸母体":
            case "§bDJ纯一郎":
                summonChest(dead.getLocation());
                w.dropItem(dead.getLocation(), weaponPool.chicken());
                break;
            case "§c类§6星体":
                summonChest(dead.getLocation().add(0,-2,0));
                break;
            case "§e金胡萝卜神的化身":
                summonChest(dead.getLocation());
                break;
        }
    }

    @EventHandler
    public void mobDamageByEntity(EntityDamageByEntityEvent damageEvent) {
        if (!(damageEvent.getEntity() instanceof LivingEntity)) return;
        LivingEntity damaged = (LivingEntity) damageEvent.getEntity();
        World w = damaged.getWorld();
        if(!(w.getName().equals("world")))return;
        if(damaged instanceof Monster && damageEvent.getDamager() instanceof Monster){
            damageEvent.setCancelled(true);
        }
        if(damageEvent.getDamager() instanceof Projectile){
            Projectile p = (Projectile) damageEvent.getDamager();
            if(p.getShooter() instanceof Monster && damaged instanceof Monster){
                damageEvent.setCancelled(true);
            }
        }
        if (damageEvent.getDamager() instanceof LivingEntity || damageEvent.getDamager() instanceof Projectile) {
            LivingEntity attacker;
            if (damageEvent.getDamager() instanceof Projectile) {
                attacker = (LivingEntity) ((Projectile) damageEvent.getDamager()).getShooter();
            } else {
                attacker = (LivingEntity) damageEvent.getDamager();
            }
            if(attacker == null)return;
            String damagedName = damaged.getName();
            String attackerName = attacker.getName();
            double damage = damageEvent.getFinalDamage();
            switch (damagedName) {
                case "§e蜂巢僵尸":
                    if (damage <= 10) {
                        if (random.nextInt(4) == 1) {
                            spawnBees(damaged.getEyeLocation(), 2, attacker);
                        }
                    } else {
                        if (random.nextBoolean()) {
                            spawnBees(damaged.getEyeLocation(), 4, attacker);
                        }
                    }
                    break;
                case "§c类§6星体":
                    if (!damaged.isGlowing()) {
                        w.playSound(damaged.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 5, 1);
                    } else {
                        w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 5, 1);
                        summonMeteor(damaged,attacker);
                    }
                    break;
                case "§7寄生僵尸":
                    w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 5, 1);
                    if(attacker instanceof Player){
                        String message = ChatColor.AQUA + "攻击寄生僵尸的本体会更有效";
                        ((Player) attacker).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                    }
                    List<Entity>passengers = damaged.getPassengers();
                    for(Entity e : passengers){
                        if(e instanceof Silverfish){
                            ((Silverfish) e).damage(3);
                        }
                    }
                    break;
                case "§b“膜术师”高资 但是分身":
                    w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 5, 1);
                    if(attacker instanceof Player){
                        ((Player) attacker).sendTitle(" ",ChatColor.AQUA + "无敌！需要攻击本体",10,20,10);
                    }
                    break;
                case "§c盗贼僵尸":
                    ItemStack hand = damaged.getEquipment().getItemInMainHand();
                    if(hand.getType() == Material.AIR)return;
                    Item item = w.dropItem(damaged.getLocation(),hand);
                    item.setPickupDelay(0);
                    item.teleport(attacker);
                    damaged.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
            }
            switch (attackerName) {
                case "§b“寒剑”塞壬":
                    damaged.setFreezeTicks(200);
                    break;
                case "§c盗贼僵尸":
                    if(damage != 0) {
                        if (random.nextInt(4) == 0) {
                            if (damaged instanceof Player) {
                                Player player = (Player) damaged;
                                ItemStack hand = player.getInventory().getItemInMainHand();
                                if (hand.getType() == Material.AIR) return;
                                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                attacker.getEquipment().setItemInMainHand(hand);
                                ability.dash(attacker,-2);
                                player.sendTitle(ChatColor.RED + "被偷窃了！", "攻击盗贼来拿回你的东西", 10, 30, 10);
                            }
                        }
                    }
            }
        }
}
    @EventHandler(priority = EventPriority.HIGHEST)
    public void mobDamage(EntityDamageEvent damageEvent){
        if(!(damageEvent.getEntity() instanceof LivingEntity))return;
        LivingEntity entity = (LivingEntity) damageEvent.getEntity();
        World w = entity.getWorld();
        if(!(w.getName().equals("world")))return;
        String name = entity.getName();
        double damage = damageEvent.getDamage();
        switch (name){
            case "§5黑檀木恶魔":
                double maxH = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if(entity.getHealth() <= maxH/2){
                    if(!entity.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 86400, 2, false));
                        w.playSound(entity.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1);
                    }
                }
                break;
            case "§e金胡萝卜神的化身":
                if(entity.isGlowing()){
                    w.playSound(entity.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                }else {
                    w.playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                }
            case "§d巫妖王":
            case "§6蜂巢僵尸母体":
            case "§bDJ纯一郎":
            case "§c“膜术师”高资":
            case "§c类§6星体":
                double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double maxDamage = maxHealth / 8;
                if(damageEvent.getCause() == EntityDamageEvent.DamageCause.CUSTOM){
                    damageEvent.setDamage(1);
                } else if(damage > maxDamage && damage < maxHealth){
                    damageEvent.setDamage(maxDamage);
                }
                break;
        }
    }
    @EventHandler
    public void split(SlimeSplitEvent splitEvent){
        Entity entity = splitEvent.getEntity();
        World w = entity.getWorld();
        if(!(w.getName().equals("world")))return;
        if(entity.getName().equals(ChatColor.RED + "类" + ChatColor.GOLD + "星体")){
            splitEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void entityPickUpItem(EntityPickupItemEvent pickupItemEvent){
        Entity entity = pickupItemEvent.getEntity();
        World w = entity.getWorld();
        if(!(w.getName().equals("world")))return;
        if(entity instanceof Monster) {
            pickupItemEvent.setCancelled(true);
        }
    }

    public void explodeLater(LivingEntity entity){
        World w = entity.getWorld();
        w.playSound(entity.getLocation(), Sound.ENTITY_TNT_PRIMED,3,1);
        BukkitRunnable explode = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.EXPLOSION_HUGE,entity.getLocation(),0);
                ability.explosion(entity,entity.getLocation(),10,3,2,false,null);
            }
        };
        explode.runTaskLater(plugin,20L);
    }public void explodeFireLater(LivingEntity entity){
        World w = entity.getWorld();
        w.playSound(entity.getLocation(), Sound.ENTITY_TNT_PRIMED,3,1);
        BukkitRunnable explode = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.EXPLOSION_HUGE,entity.getLocation(),0);
                w.playSound(entity.getLocation(),Sound.ITEM_FIRECHARGE_USE,1,1);
                w.playSound(entity.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,2);
                ability.areaDOT(entity,entity.getLocation(),100L,3,10,Particle.FLAME);
            }
        };
        explode.runTaskLater(plugin,20L);
    }
    public void shootManyBall(LivingEntity entity){
        Location shootLoc = entity.getLocation();
        Vector upVec = new Vector(0,1,0);
        for (int i = 0;i < 10;i ++ ){
            double xSpread = random.nextDouble() - random.nextDouble();
            double zSpread = random.nextDouble() - random.nextDouble();
            Vector spread = new Vector(xSpread,0,zSpread);
            Vector shootVec = (upVec.clone().add(spread)).normalize().multiply(0.5);
            ability.shootSlowBall(entity,new ItemStack(Material.SLIME_BALL),shootLoc,shootVec,6);
        }
    }
    public void shootManyArrow(LivingEntity entity){
        World w = entity.getWorld();
        Location shootLoc = entity.getLocation();
        Vector upVec = new Vector(0,1,0);
        for (int i = 0;i < 10;i ++ ){
            Arrow arrow = w.spawnArrow(shootLoc,upVec,0.7f,30);
            arrow.setDamage(15);
            arrow.setTicksLived(1200);
        }
    }
    public void spawnBees(Location location,int amount,LivingEntity damager){
        World w = location.getWorld();
        for(int i = 0;i < amount;i ++) {
            Bee bee = (Bee) w.spawnEntity(location, EntityType.BEE);
            bee.setTarget(damager);
        }
    }
    public void summonChest(Location loc){
        World w = loc.getWorld();
        Block locBlock = w.getBlockAt(loc);
        Location locClone = loc.clone();
        while (locBlock.getType()!= Material.AIR){
            locClone.add(0,1,0);
            locBlock = w.getBlockAt(locClone);
        }
        loc = locClone;
        Collection<Entity> entities = w.getNearbyEntities(loc,20,20,20);
        for(Entity e : entities){
            if(e instanceof Player){
                Player p = (Player) e;
                p.sendTitle(ChatColor.YELLOW + "等待宝箱降落"," ",0,40,10);
                p.playSound(p.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
            }
        }
        BlockData data = Bukkit.createBlockData(Material.OAK_PLANKS);
        FallingBlock block = w.spawnFallingBlock(loc.clone().add(0,50,0),data);
        block.setDropItem(false);
        block.setGlowing(true);
        Location finalLoc = loc;
        BukkitRunnable falling = new BukkitRunnable() {
            @Override
            public void run() {
                if(block.isDead()) {
                    w.getBlockAt(block.getLocation()).setType(Material.AIR);
                    w.getBlockAt(finalLoc).setType(Material.CHEST);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,block.getLocation(),1);
                    w.playSound(block.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
                    w.playSound(block.getLocation(),Sound.BLOCK_ANVIL_PLACE,2,1);
                    this.cancel();
                }
                if(w.getBlockAt(block.getLocation().add(0,-1,0)).getType() == Material.BARRIER||
                        w.getBlockAt(block.getLocation().add(0,-2,0)).getType() == Material.BARRIER){
                    block.teleport(block.getLocation().add(0,-3,0));
                }
                w.spawnParticle(Particle.LAVA,block.getLocation(),5);
            }
        };
        falling.runTaskTimer(plugin,0L,1L);
    }
    public void summonMeteor(LivingEntity owner,LivingEntity target){
        World w = target.getWorld();
        BlockData data = Bukkit.createBlockData(Material.MAGMA_BLOCK);
        FallingBlock block = w.spawnFallingBlock(target.getLocation().add(0,50,0),data);
        block.setDropItem(false);
        block.setVelocity(new Vector(0,-1,0));
        if(target instanceof Player){
            ((Player) target).sendTitle(ChatColor.GOLD + "！快离开陨石范围！", "收集陨石碎片来砸开护甲", 0, 30, 10);
        }
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(target.getLocation(),EntityType.AREA_EFFECT_CLOUD);
        cloud.setParticle(Particle.FLAME);
        cloud.setRadius(4);
        cloud.setDuration(100);
        BukkitRunnable falling = new BukkitRunnable() {
            @Override
            public void run() {
                if(block.isDead()) {
                    cloud.remove();
                    boolean drop = true;
                    w.getBlockAt(block.getLocation()).setType(Material.AIR);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,block.getLocation(),1);
                    w.playSound(block.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,5,1);
                    w.playSound(block.getLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.playSound(block.getLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.playSound(block.getLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    List<Entity>nearby = block.getNearbyEntities(4,4,4);
                    ability.areaDOT(owner,block.getLocation(),100L,4,4,Particle.LAVA);
                    for(Entity e :nearby){
                        if(e == target){
                            if(e instanceof Player) {
                                ((Player) e).sendTitle(ChatColor.RED + "你被陨石砸中了！", "没有掉落陨石碎片", 10, 30, 10);
                            }
                            target.damage(6);
                            target.setFireTicks(100);
                            if(target.getHealth() < 0){
                                Bukkit.broadcastMessage(ChatColor.RED + target.getName() + "被陨石砸死了");
                            }
                            drop = false;
                        }
                    }
                    if(drop) {
                        w.dropItem(block.getLocation(), weaponPool.meteor());
                    }
                    this.cancel();
                }
                if(w.getBlockAt(block.getLocation().add(0,-1,0)).getType() == Material.BARRIER){
                    block.teleport(block.getLocation().add(0,-2,0));
                }
                w.spawnParticle(Particle.LAVA,block.getLocation(),5);
            }
        };
        falling.runTaskTimer(plugin,0L,1L);
    }
}
