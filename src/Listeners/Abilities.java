package Listeners;
import Pool.WeaponPool;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

public class Abilities implements Listener {
    private static Abilities instance = new Abilities();

    private Abilities() {
    }

    public static Abilities getInstance() {
        return instance;
    }

    JavaPlugin plugin;
    HashSet<Projectile> projectileHits = new HashSet<>();
    HashSet<Entity> hitEntity = new HashSet<>();
    ArrayList<Location> smokeLocations = new ArrayList<>();
    HashMap<String,Integer>playerAShield = new HashMap<>();
    Random random = new Random();
    WeaponPool wp = WeaponPool.getInstance();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void projectHit(ProjectileHitEvent hitEvent) {
        Projectile projectile = hitEvent.getEntity();
        hitEntity.clear();
        if (projectileHits.size() >= 10) {
            projectileHits.clear();
        }
        if (hitEvent.getHitEntity() != null) {
            Entity entity = hitEvent.getHitEntity();
            hitEntity.add(entity);
        }
        projectileHits.add(projectile);
    }
    public ArrayList<Location> getSmokeLocations(){
        return smokeLocations;
    }

    public void eat(Player p, int amount) {
        World w = p.getWorld();
        int foodLevel = p.getFoodLevel();
        foodLevel += amount;
        if (foodLevel > 20) {
            p.setFoodLevel(20);
            p.setSaturation((foodLevel - 20) / 2.0f);
        } else {
            p.setFoodLevel(foodLevel);
        }
        w.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
    }
    public void thermiteGrenade(LivingEntity shooter, Projectile grenade, Particle particle) {
        World w = grenade.getWorld();
        grenade.setShooter(shooter);
        BukkitRunnable explode = new BukkitRunnable() {
            @Override
            public void run() {
                Location grenadeLoc = grenade.getLocation();
                w.spawnParticle(particle, grenadeLoc, 0);
                if (projectileHits.contains(grenade) || grenade.getTicksLived() >= 1200) {
                    w.playSound(grenadeLoc, Sound.ITEM_FIRECHARGE_USE, 3, 1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE, grenadeLoc, 1);
                    grenade.remove();
                    projectileHits.remove(grenade);
                    this.cancel();
                    areaDOT(shooter, grenadeLoc, 200L, 3, 6, Particle.FLAME);
                    for (int i = 0; i < 10; i++) {
                        Vector grenadeVec = grenade.getVelocity().clone().multiply(-1);
                        Vector arrVec = new Vector(0, grenadeVec.getY(), 0).normalize();
                        Arrow arrow = w.spawnArrow(grenadeLoc, arrVec, 0.6f, 30);
                        arrow.setShooter(shooter);
                        arrow.setFireTicks(1200);
                        arrow.setTicksLived(1200);
                        arrow.setDamage(10);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    }
                }
            }
        };
        explode.runTaskTimer(plugin, 0L, 1L);
    }
    public void reflectProjectile(Entity entity, Location location, Color color) {
        LivingEntity caster = (LivingEntity) entity;
        World w = caster.getWorld();
        Particle.DustOptions dust = new Particle.DustOptions(color, 1f);
        Collection<Entity> nearby = w.getNearbyEntities(location, 10, 10, 10);
        w.spawnParticle(Particle.REDSTONE, location, 50, 1, 1, 1, dust);
        int count = 0;
        for (Entity e : nearby) {
            if (e instanceof Projectile) {
                if(((Projectile) e).getShooter() == caster)continue;
                e.remove();
                w.spawnParticle(Particle.EXPLOSION_LARGE,e.getLocation(),1);
                w.playSound(e.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,1);
                count += 1;
            }
        }
        if(count == 0)return;
        int amount = count + 5;
        BukkitRunnable release = new BukkitRunnable() {
            @Override
            public void run() {
                w.playSound(caster.getEyeLocation(),Sound.BLOCK_ANVIL_PLACE,1,2);
                Location shootLoc = caster.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                for(int i = 0;i < amount;i ++){
                    Arrow arrow = w.spawnArrow(shootLoc,shootVec,4,20);
                    arrow.setColor(Color.LIME);
                    arrow.setDamage(4);
                    arrow.setTicksLived(1200);
                    arrow.setPierceLevel(10);
                    arrow.setShooter(caster);
                }
            }
        };
        release.runTaskLater(plugin,20L);
    }
    public void areaDOT(@Nullable Entity owner, Location location, Long time, double area, double damage, Particle particle) {
        World w = location.getWorld();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        cloud.setRadius((float) area);
        cloud.setParticle(particle);
        BukkitRunnable dot = new BukkitRunnable() {
            @Override
            public void run() {
                Collection<Entity> entities = w.getNearbyEntities(location, area, area, area);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity) {
                        if(owner != null) {
                            if (e == owner) continue;
                        }
                        ((LivingEntity) e).damage(damage);
                        e.setFireTicks(200);
                    }
                }
            }
        };
        BukkitRunnable cancel = new BukkitRunnable() {
            @Override
            public void run() {
                dot.cancel();
                cloud.remove();
            }
        };
        dot.runTaskTimer(plugin, 0L, 15L);
        cancel.runTaskLater(plugin, time);
    }
    public void explosion(Entity shooter, Location explodeLoc, double damage, double area, double power, boolean friendlyFire, @Nullable PotionEffect effect) {
        World w = shooter.getWorld();
        w.playSound(explodeLoc, Sound.ENTITY_GENERIC_EXPLODE, 3, 2);
        w.spawnParticle(Particle.EXPLOSION_LARGE, explodeLoc, 0);
        if (damage <= 0) return;
        Collection<Entity> entities = w.getNearbyEntities(explodeLoc, area, area, area);
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (!friendlyFire) {
                    if (e == shooter) continue;
                }
                ((LivingEntity) e).damage(damage);
                knockBack(e, explodeLoc, power);
                if (effect != null) {
                    ((LivingEntity) e).addPotionEffect(effect);
                }
            }
        }
    }
    public void knockBack(Entity entity, Location from, double power) {
        try{
            Location entityLoc = entity.getLocation();
            Vector entityVec = entityLoc.clone().toVector();
            Vector fromVec = from.clone().toVector();
            Vector knock = (entityVec.subtract(fromVec)).normalize().clone();
            entity.setVelocity(knock.normalize().multiply(power));}
        catch (Exception ignored){
        }
    }
    public void throwBoomerang(Player p, ItemStack itemStack, double damage) {
        World w = p.getWorld();
        Vector shootVec = p.getEyeLocation().getDirection();
        Item item = w.dropItem(p.getEyeLocation(), itemStack);
        item.setVelocity(shootVec.multiply(2));
        item.setOwner(p.getUniqueId());
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                if (item.isDead()) this.cancel();
                if (item.isOnGround()) {
                    item.setPickupDelay(0);
                    item.teleport(p);
                }
                w.spawnParticle(Particle.CRIT, item.getLocation(), 0);
                Collection<Entity> entities = w.getNearbyEntities(item.getLocation(), 0.5, 0.5, 0.5);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity) {
                        if (e.isDead()) continue;
                        if (!(e instanceof Player)) {
                            ((LivingEntity) e).damage(damage);
                            item.teleport(p);
                            item.setPickupDelay(0);
                        }
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void throwMagicBoomerang(Player p, ItemStack itemStack, double damage) {
        World w = p.getWorld();
        Vector shootVec = p.getEyeLocation().getDirection();
        Item item = w.dropItem(p.getEyeLocation(), itemStack);
        item.setVelocity(shootVec.multiply(2));
        item.setOwner(p.getUniqueId());
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        HashSet<Entity> previous = new HashSet<>();
        BukkitRunnable hit = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (item.isDead()) this.cancel();
                if (item.isOnGround() || count > 3) {
                    item.setPickupDelay(0);
                    item.teleport(p);
                }
                w.spawnParticle(Particle.CRIT_MAGIC, item.getLocation(), 0);
                Collection<Entity> entities = w.getNearbyEntities(item.getLocation(), 0.5, 0.5, 0.5);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity) {
                        if (!(e instanceof Player)) {
                            count += 1;
                            ((LivingEntity) e).damage(damage);
                            previous.add(e);
                            Vector itemVec = item.getVelocity().clone().multiply(-1);
                            item.setVelocity(new Vector(itemVec.getX(), 0, itemVec.getZ()).normalize());
                            item.setPickupDelay(0);
                        }
                    }
                }
                Collection<Entity> nearbyEntities = w.getNearbyEntities(item.getLocation(), 5, 5, 5);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity) {
                        if (entity.isDead()) continue;
                        if (previous.contains(entity)) continue;
                        if (!(entity instanceof Player)) {
                            Location entityLoc = ((LivingEntity) entity).getEyeLocation();
                            knockBack(item, entityLoc, -1);
                        }
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void throwBombAxe(Player p, ItemStack itemStack, double damage) {
        World w = p.getWorld();
        Vector shootVec = p.getEyeLocation().getDirection();
        Item item = w.dropItem(p.getEyeLocation(), itemStack);
        item.setVelocity(shootVec.multiply(2));
        item.setOwner(p.getUniqueId());
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        HashSet<Entity> previous = new HashSet<>();
        BukkitRunnable hit = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (item.isDead()) this.cancel();
                if (item.isOnGround() || count > 5) {
                    item.setPickupDelay(0);
                    item.teleport(p);
                }
                w.spawnParticle(Particle.CRIT_MAGIC, item.getLocation(), 0);
                Collection<Entity> entities = w.getNearbyEntities(item.getLocation(), 0.5, 0.5, 0.5);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity) {
                        if (!(e instanceof Player)) {
                            count += 1;
                            explosion(p, item.getLocation(), damage, 3, 1, false, null);
                            previous.add(e);
                            Vector itemVec = item.getVelocity().clone().multiply(-1);
                            item.setVelocity(new Vector(itemVec.getX(), 0, itemVec.getZ()).normalize());
                            item.setPickupDelay(0);
                        }
                    }
                }
                Collection<Entity> nearbyEntities = w.getNearbyEntities(item.getLocation(), 5, 5, 5);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity) {
                        if (entity.isDead()) continue;
                        if (previous.contains(entity)) continue;
                        if (!(entity instanceof Player)) {
                            Location entityLoc = ((LivingEntity) entity).getEyeLocation();
                            knockBack(item, entityLoc, -1);
                        }
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void shootMagic(Player p, int amount, int pierce,float speed, double damage,double spread, Color color) {
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        int quantity = amount / 2;
        double finalSpread = spread * 0.1;
        for (int i = -quantity; i <= quantity; i++) {
            Vector forwardVec = p.getEyeLocation().getDirection().normalize();
            Vector downVec = new Vector(0, -1, 0).normalize();
            Vector rightVec = forwardVec.clone().crossProduct(downVec);
            Arrow arrow = w.spawnArrow(shootLoc, shootVec.clone().add(rightVec.clone().multiply(finalSpread * i)), speed, 0);
            arrow.setDamage(damage);
            arrow.setShooter(p);
            arrow.setPierceLevel(pierce);
            arrow.setColor(color);
            arrow.setTicksLived(1200);
        }
    }
    public void shootDelayedMagic(Player p, int amount, int pierce,float speed, double damage,double spread, Color color) {
        World w = p.getWorld();
        Vector shootVec = p.getEyeLocation().getDirection();
        BukkitRunnable delay = new BukkitRunnable() {
            @Override
            public void run() {
                int quantity = amount / 2;
                double finalSpread = spread * 0.1;
                for (int i = -quantity; i <= quantity; i++) {
                    Location shootLoc = p.getEyeLocation();
                    Vector forwardVec = shootVec.normalize();
                    Vector downVec = new Vector(0, -1, 0).normalize();
                    Vector rightVec = forwardVec.clone().crossProduct(downVec);
                    Arrow arrow = w.spawnArrow(shootLoc, shootVec.clone().add(rightVec.clone().multiply(finalSpread * i)), speed, 0);
                    arrow.setDamage(damage);
                    arrow.setShooter(p);
                    arrow.setPierceLevel(pierce);
                    arrow.setColor(color);
                    arrow.setTicksLived(1200);
                }
            }
        };
        delay.runTaskLater(plugin,10L);
    }
    public void shootFreezeMagic(Player p, int amount, int pierce,float speed, double damage,double spread, Color color) {
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        int quantity = amount / 2;
        double finalSpread = spread * 0.1;
        for (int i = -quantity; i <= quantity; i++) {
            Vector forwardVec = p.getEyeLocation().getDirection().normalize();
            Vector downVec = new Vector(0, -1, 0).normalize();
            Vector rightVec = forwardVec.clone().crossProduct(downVec);
            Arrow arrow = w.spawnArrow(shootLoc, shootVec.clone().add(rightVec.clone().multiply(finalSpread * i)), speed, 0);
            arrow.setDamage(damage);
            arrow.setShooter(p);
            arrow.setPierceLevel(pierce);
            arrow.setColor(color);
            arrow.setFreezeTicks(200);
            arrow.setTicksLived(1200);
        }
    }
    public void shootSlowBall(LivingEntity shooter, ItemStack type, Location shootLoc, Vector shootVec, double damage) {
        World w = shooter.getWorld();
        Snowball ball = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
        ball.setItem(new ItemStack(type));
        ball.setVelocity(shootVec);
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                if (ball.isDead()) this.cancel();
                w.spawnParticle(Particle.ITEM_CRACK, ball.getLocation(), 0, 0, 0, 0, type);
                Collection<Entity> entities = w.getNearbyEntities(ball.getLocation(), 0.5, 0.5, 0.5);
                for (Entity e : entities) {
                    if (e.isDead()) continue;
                    if (e == shooter) continue;
                    if (e instanceof LivingEntity) {
                        ball.remove();
                        ((LivingEntity) e).damage(damage);
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0));
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void areaDamage(Location playerLoc, double area, double damage) {
        World w = playerLoc.getWorld();
        Collection<Entity> entities = w.getNearbyEntities(playerLoc, area, area, area);
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (!(e instanceof Player)) {
                    ((LivingEntity) e).damage(damage);
                }
            }
        }
    }
    public void dash(LivingEntity entity, double power) {
        Vector entityVec = entity.getEyeLocation().getDirection().normalize().clone();
        Vector dashVec = new Vector(entityVec.getX(), 0.05, entityVec.getZ());
        entity.setVelocity(dashVec.clone().normalize().multiply(power));
    }
    public void fireWall(LivingEntity entity) {
        World w = entity.getWorld();
        Vector entityVec = entity.getEyeLocation().getDirection().clone();
        Vector wallVec = new Vector(entityVec.getX(), 0, entityVec.getZ());
        w.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        for (int i = 0; i < 8; i++) {
            Location fireLoc = entity.getLocation().add(wallVec.clone().multiply(2.5 * i));
            w.playSound(fireLoc, Sound.ITEM_FIRECHARGE_USE, 1, 1);
            BukkitRunnable fireClimb = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.EXPLOSION_LARGE, fireLoc, 0);
                    areaDOT(entity, fireLoc, 200L, 1, 20, Particle.FLAME);
                }
            };
            fireClimb.runTaskLater(plugin, i * 3L);
        }
    }
    public void arcWave(LivingEntity entity) {
        Vector entityVec = entity.getEyeLocation().getDirection().clone();
        Vector waveVec = new Vector(entityVec.getX(), 0, entityVec.getZ());
        PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 200, 1);
        for (int i = 0; i < 8; i++) {
            Location arcLoc = entity.getLocation().add(waveVec.clone().multiply(2.5 * i));
            BukkitRunnable fireClimb = new BukkitRunnable() {
                @Override
                public void run() {
                    arcWaveEffect(arcLoc);
                    explosion(entity, arcLoc, 30, 1, 0, false, effect);
                }
            };
            fireClimb.runTaskLater(plugin, i * 3L);
        }
    }
    public void arcWaveEffect(Location loc) {
        World w = loc.getWorld();
        Vector up = new Vector(0, 0.16, 0);
        Location arcLoc = loc.clone();
        Particle.DustOptions dust = new Particle.DustOptions(Color.AQUA, 1f);
        for (int i = 0; i < 30; i++) {
            arcLoc.add(up.clone());
            w.spawnParticle(Particle.REDSTONE, arcLoc, 1, 0.2, 0, 0.2, dust);
        }
    }
    public void suck(Entity shooter, Location explodeLoc, double area, double power) {
        World w = shooter.getWorld();
        Collection<Entity> entities = w.getNearbyEntities(explodeLoc, area, area, area);
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (e instanceof Animals) continue;
                if (e == shooter) continue;
                knockBack(e, explodeLoc, -power);
            }
        }
    }
    public void coldSmoke(Entity owner, Location location, Long time, double damage) {
        World w = owner.getWorld();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        cloud.setRadius(1);
        cloud.setParticle(Particle.EXPLOSION_HUGE);
        BukkitRunnable dot = new BukkitRunnable() {
            @Override
            public void run() {
                Collection<Entity> entities = w.getNearbyEntities(location, 3, 3, 3);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity) {
                        if (e instanceof Animals) continue;
                        if (e == owner) continue;
                        ((LivingEntity) e).damage(damage);
                        e.setFreezeTicks(200);
                    }
                }
            }
        };
        BukkitRunnable cancel = new BukkitRunnable() {
            @Override
            public void run() {
                dot.cancel();
                cloud.remove();
            }
        };
        dot.runTaskTimer(plugin, 0L, 25L);
        cancel.runTaskLater(plugin, time);
    }
    public void healthDrain(Player p) {
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation().clone();
        Vector shootVec = shootLoc.getDirection().clone().normalize();
        Arrow ray = w.spawnArrow(shootLoc,shootVec,5,0);
        ray.setColor(Color.AQUA);
        ray.setGlowing(true);
        ray.setTicksLived(1200);
        ray.setDamage(3);
        ray.setShooter(p);
        w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
        w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
        w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
        w.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                if (projectileHits.contains(ray)) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.SLOW,100,1);
                    explosion(p, ray.getLocation(), 10, 3, 0, false, effect);
                    if(!hitEntity.isEmpty()) {
                        double health = p.getHealth();
                        p.setHealth(Math.min(20, health + 6));
                    }
                    this.cancel();
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 2L);
    }
    public void missiles(Player p,boolean boosted) {
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation().clone();
        Vector shootVec = shootLoc.getDirection().clone().normalize();
        Vector xVec = (new Vector(shootVec.getY(),shootVec.getZ(),shootVec.getX())).normalize();
        Vector zVec = (shootVec.clone().crossProduct(xVec.clone())).normalize();
        ArrayList<Location>locations = new ArrayList<>();
        double i = Math.PI;
        int amount = 6;
        if(boosted){
            amount = 12;
        }
        for (int j = 0; j < amount; j++) {
            double x = (Math.sin(i + j));
            double z = (Math.cos(i + j));
            Vector vectorX = xVec.clone().multiply(x);
            Vector vectorY = shootVec.clone().multiply(0);
            Vector vectorZ = zVec.clone().multiply(z);
            Location loc = (vectorX.clone().add(vectorY.clone()).add(vectorZ.clone())).toLocation(w);
            locations.add(loc);
        }
        BukkitRunnable shoot = new BukkitRunnable() {
            @Override
            public void run() {
                if(locations.size() == 0){
                    this.cancel();
                    return;
                }
                Location finalShootLoc = p.getEyeLocation().clone();
                Vector finalShootVec = p.getEyeLocation().getDirection().clone();
                finalShootLoc.add(finalShootVec.multiply(2));
                Arrow arrow = w.spawnArrow(locations.get(0).add(finalShootLoc),finalShootVec,3,0);
                arrow.setDamage(5);
                arrow.setColor(Color.ORANGE);
                arrow.setTicksLived(1200);
                arrow.setShooter(p);
                w.spawnParticle(Particle.CLOUD,arrow.getLocation(),10,0,0,0,0.02);
                w.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0.9f);
                locations.remove(0);
            }
        };
        shoot.runTaskTimer(plugin,0L,2L);
    }
    public void throwMeteor(Player p,ItemStack stack) {
        World w = p.getWorld();
        Vector shootVec = p.getEyeLocation().getDirection();
        Snowball snowball = (Snowball) w.spawnEntity(p.getEyeLocation(),EntityType.SNOWBALL);
        snowball.setItem(stack);
        snowball.setVelocity(shootVec.multiply(2));
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        w.playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
        int amount = stack.getAmount();
        stack.setAmount(amount - 1);
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                if(snowball.isDead())this.cancel();
                if(projectileHits.contains(snowball)){
                    explosion(p,snowball.getLocation(),5,3,1,false,null);
                    List<Entity>nearby = snowball.getNearbyEntities(3,3,3);
                    for(Entity e:nearby){
                        if(e instanceof MagmaCube){
                            MagmaCube cube = (MagmaCube) e;
                            if(e.getName().equals(ChatColor.RED + "类" + ChatColor.GOLD + "星体")){
                                if(!cube.isGlowing()){
                                    ((MagmaCube) e).damage(10);
                                    return;
                                }
                                p.sendTitle(ChatColor.GOLD + "Boss的装甲被炸开了！","对Boss使用武器吧！",10,30,10);
                                w.playSound(cube.getLocation(),Sound.ENTITY_WITHER_BREAK_BLOCK,5,1);
                                cube.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                cube.setGlowing(false);
                                w.dropItem(p.getLocation(),wp.chicken());
                                BukkitRunnable returnEffect = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        cube.setGlowing(true);
                                        cube.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,5));
                                    }
                                };
                                returnEffect.runTaskLater(plugin,100L);
                            }
                        }
                    }
                    this.cancel();
                    return;
                }
                w.spawnParticle(Particle.LAVA, snowball.getLocation(), 0);
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void universalSlash(Location loc, int radius1, double angle1, double angle2,Color color) {
        World w = loc.getWorld();
        Vector upVector = new Vector(0, 1, 0);
        double playerX = loc.getX();
        double playerZ = loc.getZ();
        Vector playerEye1 = (loc.getDirection()).normalize();
        Vector angleVector = ((playerEye1.clone()).crossProduct(upVector)).normalize();
        Vector playerEy = playerEye1.add(angleVector.multiply(0.1));
        Vector playerEye = (playerEy.normalize());
        Vector leftVector = (((playerEye.clone()).crossProduct(upVector)).normalize());
        Vector rightVector = (leftVector.clone()).normalize();
        Vector upRight = ((upVector.clone()).multiply(angle2)).add((rightVector.clone()).multiply(angle1));
        Vector downLeft = ((upRight.clone()).multiply(-1));
        Particle.DustOptions dust = new Particle.DustOptions(color,1.2f);
        for (double j = 0.065; j <= 3.6; j += 0.035) {
            double x = (Math.sin(j - 1.57)) * (Math.sqrt(radius1) + 1);
            double z = (Math.cos(j - 1.57)) * (Math.sqrt(radius1) + 1);
            Vector left = ((downLeft.clone()).normalize()).multiply(x);
            Vector forward = ((playerEye.clone()).normalize()).multiply(z);
            Vector particleLoc = ((left.clone()).add(forward.clone())).add((upVector.clone()).multiply(1.2));
            Location areaP = new Location(w, playerX, loc.getY(), playerZ);
            areaP = (areaP.add(particleLoc)).add((playerEye1.clone()).multiply(2));
            w.spawnParticle(Particle.REDSTONE, areaP, 0,0,0,0,dust);
        }
    }
    public void masterSword(Player p){
        World w = p.getWorld();
        Vector forwardVec = p.getEyeLocation().getDirection().clone();
        Location forwardLoc = p.getEyeLocation().clone();
        forwardLoc.add(0,-1,0);
        double angle1 = random.nextDouble() - random.nextDouble();
        double angle2 = random.nextDouble() - random.nextDouble();
        BukkitRunnable slash = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                w.playSound(forwardLoc,Sound.BLOCK_AMETHYST_BLOCK_BREAK,2,1);
                if(count == 0 || count % 3 == 0){
                    Collection<Entity> entities = w.getNearbyEntities(forwardLoc, 5, 5, 5);
                    for (Entity e : entities) {
                        if (e instanceof LivingEntity) {
                            if (!(e instanceof Player)) {
                                String name = e.getName();
                                switch (name){
                                    case "§e金胡萝卜神的化身":
                                    case "§d巫妖王":
                                    case "§6蜂巢僵尸母体":
                                    case "§bDJ纯一郎":
                                    case "§c“膜术师”高资":
                                    case "§c类§6星体":
                                        ((LivingEntity) e).damage(5);
                                    default:
                                        ((LivingEntity) e).damage(25);
                                }
                            }
                        }
                    }
                }
                if(count >= 10){
                    this.cancel();
                    return;
                }
                universalSlash(forwardLoc,10,angle1,angle2,Color.AQUA);
                forwardLoc.add(forwardVec.clone().multiply(1.5));
                count += 1;
            }
        };
        slash.runTaskTimer(plugin,0L,2L);
    }
    public void shieldAttack(LivingEntity attacker,LivingEntity damaged,double power,double damage){
        World w = attacker.getWorld();
        w.playSound(attacker.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
        Location attackerLoc = attacker.getLocation().clone();
        Location damagedLoc = damaged.getLocation().clone();
        Vector knockBack = (damagedLoc.subtract(attackerLoc)).toVector().normalize().clone();
        BukkitRunnable hit = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 2){
                    this.cancel();
                    return;
                }
                damaged.setVelocity(knockBack.clone().multiply(power));
                Location damagedEyeLoc = damaged.getEyeLocation().clone();
                if(w.getBlockAt(damagedEyeLoc.add(knockBack.clone())).getType() != Material.AIR){
                    damaged.damage(damage);
                    damaged.setVelocity(new Vector(0,0,0));
                    w.playSound(damaged.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
                    this.cancel();
                    return;
                }
                double area = 1 + 0.5 * power - 1;
                List<Entity>nearby = damaged.getNearbyEntities(area,area,area);
                int amount = 0;
                for(Entity e : nearby){
                    if(e instanceof Mob){
                        if(e.isDead())continue;
                        ((Mob) e).damage(damage);
                        e.setVelocity(damaged.getVelocity());
                        w.playSound(e.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
                        amount += 1;
                    }
                }
                if(amount > 0){
                    damaged.damage(damage);
                    damaged.setVelocity(new Vector(0,0,0));
                    w.playSound(damaged.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
                    this.cancel();
                    return;
                }
                count += 1;
            }
        };
        hit.runTaskTimer(plugin,0L,3L);
    }
    public void shieldRush(Player p,double power,double damage){
        World w = p.getWorld();
        w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,40,5));
        if(((Entity)p).isOnGround()){
            dash(p,3);
        }else {
            dash(p,1.5);
        }
        BukkitRunnable dash = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                count += 1;
                int amount = 0;
                List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                for (Entity e : nearby) {
                    if (e instanceof Mob) {
                        if(e.isDead())continue;
                        amount += 1;
                        ((Mob) e).damage(damage/2);
                        shieldAttack(p, (LivingEntity) e,power,damage);
                        amount += 1;
                    }
                }
                if(amount > 0 || count > 10){
                    if(!p.isBlocking()) {
                        p.setVelocity(new Vector(0, 0, 0));
                    }
                    this.cancel();
                }
            }
        };
        dash.runTaskTimer(plugin, 0L, 2L);
    }
    public void aShieldAbility(Player p){
        String name = p.getName();
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        int aStatus = playerAShield.getOrDefault(name,0);
        switch (aStatus){
            case 0:
                w.playSound(shootLoc, Sound.ENTITY_GENERIC_SWIM, 2, 2);
                w.playSound(shootLoc, Sound.ENTITY_GENERIC_SWIM, 2, 2);
                w.playSound(shootLoc, Sound.ENTITY_GENERIC_SWIM, 2, 2);
                w.playSound(shootLoc, Sound.ENTITY_GENERIC_SWIM, 2, 2);
                shootMagic(p,15,10,3,5,0.5,Color.AQUA);
                break;
            case 1:
                w.playSound(shootLoc, Sound.ITEM_FIRECHARGE_USE, 2, 1);
                for (int i = 0; i < 20; i++) {
                    Arrow fire = w.spawnArrow(shootLoc, shootVec, 1, 30);
                    fire.setFireTicks(1200);
                    fire.setTicksLived(1200);
                    fire.setShooter(p);
                    fire.setDamage(25);
                }
                break;
            case 2:
                aEarth(p);
                break;
            case 3:
                aAir(p);
                break;
        }
        aStatus += 1;
        if(aStatus > 3)
            playerAShield.remove(name);
        else
            playerAShield.put(name,aStatus);
    }
    public double lerp(double s,double e,double t){
        return s * (1 - t) + e * t;
    }
    public int distance(Location loc1,Location loc2){
        int x = (int)loc2.getX() - (int)loc1.getX();
        int z = (int)loc2.getZ() - (int)loc1.getZ();
        return Math.max(Math.abs(x),Math.abs(z));
    }
    public Location lerpLoc(Location loc1,Location loc2,double t){
        World w = loc1.getWorld();
        return new Location(w,lerp(loc1.getX(),loc2.getX(),t),loc1.getY(),lerp(loc1.getZ(),loc2.getZ(),t));
    }
    public Location roundLoc(Location loc){
        World w = loc.getWorld();
        return new Location(w,(int)loc.getX(),loc.getY(),(int)loc.getZ());
    }
    public List<Location> drawBlockLine(Location from, Location to){
        List<Location>locations = new ArrayList<>();
        int distance = distance(from,to);
        for(int i = 0;i <= distance;i++){
            double t;
            if(distance == 0){
                t = 0;
            }else {
                t = i * 1.0/ distance ;
            }
            locations.add(roundLoc(lerpLoc(from,to,t)));
        }
        return locations;
    }
    public void aEarth(Player p) {
        World w = p.getWorld();
        Location pLoc = p.getLocation().clone();
        Location eLoc = p.getEyeLocation().clone();
        Vector eVec = eLoc.getDirection().clone();
        Vector finalEVec = new Vector(eVec.getX(), 0, eVec.getZ()).normalize().clone();
        Vector uVec = new Vector(0, 1, 0).clone();
        Vector lVec = ((finalEVec.clone()).crossProduct((uVec)).normalize());
        Location wallLoc = pLoc.add(finalEVec.clone().multiply(3));
        Location leftLoc = wallLoc.clone().add(lVec.multiply(1));
        Location rightLoc = wallLoc.clone().add(lVec.multiply(-1));
        List<Location>locations = new ArrayList<>();
        Collection<Entity>entities = w.getNearbyEntities(wallLoc.clone().add(0,1,0),3,3,3);
        for (int y = 0; y < 3; y++) {
            List<Location> locList = drawBlockLine(leftLoc,rightLoc);
            for (Location loc : locList){
                loc.add(0,y,0);
                if(w.getBlockAt(loc).getType() == Material.AIR){
                    w.getBlockAt(loc).setType(Material.DIRT);
                    w.playSound(loc,Sound.BLOCK_GRAVEL_PLACE,1,0.8f);
                }
            }
            locations.addAll(locList);
        }
        for(Entity e:entities){
            if(e instanceof Mob){
                ((Mob) e).damage(10);
                w.playSound(e.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                w.playSound(e.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                w.playSound(e.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                shieldAttack(p, (LivingEntity) e,4,30);
            }
        }
        BukkitRunnable removeBlock = new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : locations) {
                    w.getBlockAt(loc).setType(Material.AIR);
                    w.playSound(loc, Sound.BLOCK_GRAVEL_BREAK, 1, 0.8f);
                    w.spawnParticle(Particle.BLOCK_CRACK, loc, 10, 0, 0, 0, 0.2, Bukkit.createBlockData(Material.DIRT));
                }
            }
        };
        removeBlock.runTaskLater(plugin, 40L);
    }
    public void aAir(Player p){
        World w = p.getWorld();
        p.setVelocity(new Vector(0,0.5,0));
        Location loc = p.getLocation();
        BukkitRunnable air = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 3) {
                    this.cancel();
                    return;
                }
                if(count == 0){
                    w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_RIPTIDE_3,1,1);
                    w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_RIPTIDE_3,1,1);
                    w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_RIPTIDE_3,1,1);
                    List<Entity>entities = p.getNearbyEntities(6,6,6);
                    for(Entity e : entities){
                        if(e instanceof Mob){
                            shieldAttack(p, (LivingEntity) e,3,30);
                        }
                    }
                }
                w.spawnParticle(Particle.EXPLOSION_LARGE,loc,10,count*3,0,count*3);
                count += 1;
            }
        };
        air.runTaskTimer(plugin,0L,4L);
    }
    public void rainArrow(Player p){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        Arrow rain = w.spawnArrow(shootLoc,shootVec,3,1);
        rain.setShooter(p);
        rain.setDamage(5);
        rain.setTicksLived(1200);
        rain.setColor(Color.WHITE);
        BukkitRunnable rainBow = new BukkitRunnable() {
            @Override
            public void run() {
                if(projectileHits.contains(rain)){
                    Location rainLoc = rain.getLocation().add(0,5,0);
                    BukkitRunnable summonRain = new BukkitRunnable() {
                        @Override
                        public void run() {
                            w.playSound(rainLoc,Sound.ENTITY_ARROW_SHOOT,2,1);
                            w.playSound(rainLoc,Sound.ENTITY_ARROW_SHOOT,2,1);
                            w.playSound(rainLoc,Sound.ENTITY_ARROW_SHOOT,2,1);
                            w.playSound(rainLoc,Sound.ENTITY_ARROW_SHOOT,2,1);
                            w.playSound(rainLoc,Sound.ENTITY_ARROW_SHOOT,2,1);
                            for(int i = 0;i < 10;i ++){
                                Arrow arrow = w.spawnArrow(rainLoc,new Vector(0,-1,0),1,50);
                                arrow.setTicksLived(1200);
                                arrow.setDamage(0);
                                arrow.setColor(Color.WHITE);
                                arrow.setShooter(p);
                            }
                            areaDamage(rain.getLocation(),4,15);
                        }
                    };
                    this.cancel();
                    summonRain.runTaskLater(plugin,20L);
                }
            }
        };
        rainBow.runTaskTimer(plugin,0L,2L);
    }
    public void smokeArrow(Player p,float force){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        w.playSound(shootLoc,Sound.ENTITY_ARROW_SHOOT,1,1);
        w.playSound(shootLoc,Sound.ENTITY_ARROW_SHOOT,1,1);
        Arrow smoke = w.spawnArrow(shootLoc,shootVec,2.5f * force,1);
        smoke.setShooter(p);
        smoke.setDamage(5);
        smoke.setTicksLived(1200);
        smoke.setColor(Color.GRAY);
        BukkitRunnable smokeHit = new BukkitRunnable() {
            @Override
            public void run() {
                if(projectileHits.contains(smoke)) {
                    Location smokeLoc = smoke.getLocation().clone();
                    smokeLocations.add(smokeLoc);
                    BukkitRunnable remove = new BukkitRunnable() {
                        @Override
                        public void run() {
                            smokeLocations.remove(smokeLoc);
                        }
                    };
                    remove.runTaskLater(plugin,200L);
                    AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(smokeLoc, EntityType.AREA_EFFECT_CLOUD);
                    cloud.setColor(Color.GRAY);
                    cloud.setRadius(5);
                    cloud.setDuration(200);
                    this.cancel();
                }
            }
        };
        smokeHit.runTaskTimer(plugin,0L,2L);
    }
    public void carrotBall(LivingEntity entity) {
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector upVec = new Vector(0, 1, 0);
        for (int i = 0; i < 10; i++) {
            w.playSound(shootLoc, Sound.BLOCK_SLIME_BLOCK_PLACE, 2, 1);
            double xSpread = random.nextDouble() - random.nextDouble();
            double zSpread = random.nextDouble() - random.nextDouble();
            Vector spread = new Vector(xSpread, 0, zSpread);
            Vector shootVec = (upVec.clone().add(spread.clone().multiply(1.5))).normalize().multiply(0.4);
            shootSlowBall(entity, new ItemStack(Material.SLIME_BALL), shootLoc, shootVec, 6);
        }
    }
    public void carrotArrow(LivingEntity entity) {
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector upVec = new Vector(0, 0.5, 0);
        for (int i = 0; i < 10; i++) {
            w.playSound(shootLoc, Sound.ITEM_CROSSBOW_SHOOT, 2, 1);
            Arrow arrow = w.spawnArrow(shootLoc, upVec, 0.5f, 50);
            arrow.setDamage(15);
            arrow.setTicksLived(1200);
        }
    }
    public void carrotBee(LivingEntity entity,Player target) {
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        for (int i = 0; i < 4; i++) {
            w.playSound(shootLoc, Sound.BLOCK_BEEHIVE_ENTER, 2, 1);
            Bee bee = (Bee) w.spawnEntity(shootLoc, EntityType.BEE);
            bee.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 86400, 0));
            bee.setTarget(target);
            bee.setVelocity(shootLoc.getDirection());
        }
    }
    public void carrotAnvil(LivingEntity entity) {
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        entity.setAI(true);
        entity.setGlowing(false);
        w.playSound(shootLoc, Sound.ENTITY_IRON_GOLEM_HURT, 2, 1);
        BlockData data = Bukkit.createBlockData(Material.ANVIL);
        FallingBlock anvil = w.spawnFallingBlock(shootLoc, data);
        anvil.setDropItem(false);
        anvil.setVelocity(shootVec.multiply(2).clone());
        BukkitRunnable hit = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (anvil.isDead() || anvil.isOnGround()) {
                    w.spawnParticle(Particle.BLOCK_CRACK, anvil.getLocation(), 20, 0, 0, 0, 0.4, data);
                    this.cancel();
                    anvil.remove();
                    w.getBlockAt(anvil.getLocation()).setType(Material.AIR);
                    return;
                }
                count += 1;
                List<Entity> nearby = anvil.getNearbyEntities(2, 2, 2);
                for (Entity e : nearby) {
                    if (e == entity) continue;
                    if (e instanceof Player) {
                        ((Player) e).damage(10);
                        if (((Player) e).getHealth() <= 0) {
                            if (random.nextInt(10) == 1) {
                                Bukkit.broadcastMessage(ChatColor.RED + e.getName() + "用弓箭射中了一个末影人,被末影人用铁砧砸死了");
                            } else {
                                Bukkit.broadcastMessage(ChatColor.RED + e.getName() + "被铁砧砸死了");
                            }
                        }
                        w.playSound(((Player) e).getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 2, 0.7f);
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                        knockBack(e, anvil.getLocation(), 2);
                        anvil.remove();
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void carrotChicken(LivingEntity entity) {
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector upVec = new Vector(0, 1, 0);
        BukkitRunnable chickenSpawn = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count > 5 || entity.isDead()) {
                    this.cancel();
                    return;
                }
                count += 1;
                w.playSound(shootLoc, Sound.ENTITY_CHICKEN_HURT, 2, 1);
                double xSpread = random.nextDouble() - random.nextDouble();
                double zSpread = random.nextDouble() - random.nextDouble();
                Vector spread = new Vector(xSpread, 0, zSpread);
                Vector shootVec = (upVec.clone().add(spread)).normalize().multiply(0.5);
                ItemStack chicken = new ItemStack(Material.COOKED_CHICKEN);
                ItemMeta chickenMeta = chicken.getItemMeta();
                chickenMeta.setDisplayName(ChatColor.GOLD + "数一数二的烧鸡");
                chickenMeta.setLocalizedName("" + count);
                chicken.setItemMeta(chickenMeta);
                Item item = w.dropItem(shootLoc, chicken);
                item.setVelocity(shootVec);
            }
        };
        chickenSpawn.runTaskTimer(plugin, 0L, 5L);
    }
    public void carrotDisarm(LivingEntity entity,Player target) {
        World w = entity.getWorld();
        ItemStack hand = target.getInventory().getItemInMainHand();
        if (hand.getType() != Material.AIR) {
            w.dropItem(target.getLocation(), hand);
            target.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            target.playSound(target.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }
    public void carrotDash(LivingEntity entity){
        World w = entity.getWorld();
        BukkitRunnable count = new BukkitRunnable() {
            int num = 0;
            @Override
            public void run() {
                if(num > 3 || entity.isDead()){
                    this.cancel();
                    return;
                }
                if(num == 0) {
                    Location dashLoc = entity.getEyeLocation();
                    Vector dashVec = entity.getEyeLocation().getDirection().clone();
                    Vector finalVec = new Vector(dashVec.getX(), 0, dashVec.getZ());
                    for (int i = 0; i < 8; i++) {
                        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(dashLoc, EntityType.AREA_EFFECT_CLOUD);
                        cloud.setDuration(15);
                        cloud.setRadius(2);
                        cloud.setColor(Color.LIME);
                        dashLoc.add(finalVec.clone().multiply(2));
                    }
                    entity.setGlowing(true);
                    entity.setAI(false);
                }
                if(num < 2){
                    w.playSound(entity.getLocation(),Sound.BLOCK_GRASS_BREAK,2,1);
                }
                if(num == 2){
                    w.playSound(entity.getLocation(),Sound.ENTITY_COW_HURT,2,1);
                }
                if(num == 3){
                    entity.setGlowing(false);
                    entity.setAI(true);
                    Vector dashVec = entity.getEyeLocation().getDirection().clone();
                    Vector finalVec = new Vector(dashVec.getX(),0,dashVec.getZ());
                    BukkitRunnable dash = new BukkitRunnable() {
                        int count = 0;
                        @Override
                        public void run() {
                            if(entity.isDead()){
                                this.cancel();
                                return;
                            }
                            if(count >= 3){
                                this.cancel();
                                return;
                            }
                            count += 1;
                            entity.setVelocity(finalVec.multiply(1.5));
                            List<Entity>nearby = entity.getNearbyEntities(2,2,2);
                            for(Entity e : nearby){
                                if(e instanceof Player){
                                    ((Player) e).damage(10);
                                    if(((Player) e).getHealth() <= 0){
                                        Bukkit.broadcastMessage(ChatColor.RED + e.getName() + "被牛头人撞死了");
                                    }
                                    entity.attack(e);
                                    entity.setVelocity(new Vector(0,0,0));
                                    this.cancel();
                                }else if(e instanceof LivingEntity){
                                    suck(entity,entity.getLocation(),2,-2);
                                }
                            }
                        }
                    };
                    dash.runTaskTimer(plugin,0L,5L);
                }
                num += 1;
            }
        };
        count.runTaskTimer(plugin,0L,10L);
    }
    public void carrotMagic(LivingEntity entity){
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        for (int i = 0; i < 10; i++) {
            Arrow arrow = w.spawnArrow(shootLoc, shootVec, 2, 12);
            arrow.setDamage(4);
            arrow.setTicksLived(1200);
            arrow.setShooter(entity);
            arrow.setColor(Color.PURPLE);
            arrow.setCustomName(ChatColor.LIGHT_PURPLE + "巫妖王的法术");
        }
    }
    public void carrotEarthShake(LivingEntity entity,Player target){
        World w = entity.getWorld();
        BukkitRunnable count = new BukkitRunnable() {
            int num = 0;
            @Override
            public void run() {
                if(num > 3 || entity.isDead()){
                    this.cancel();
                    return;
                }
                if(num < 2){
                    w.spawnParticle(Particle.EXPLOSION_LARGE,entity.getEyeLocation(),0);
                    w.playSound(entity.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(entity.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(entity.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(entity.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                }
                if(num == 2) {
                    if (((Entity) target).isOnGround()) {
                        target.damage(8);
                        target.sendTitle(ChatColor.RED + "Oops！", "Boss的撼地攻击需要跳起来躲避", 10, 30, 10);
                    }
                    w.spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 50, 10, 0, 10);
                    w.playSound(entity.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
                }
                num += 1;
            }
        };
        count.runTaskTimer(plugin,0L,20L);
    }
    public void carrotDragonShout(LivingEntity entity){
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        ArmorStand stand = (ArmorStand) w.spawnEntity(entity.getLocation().add(shootVec.clone()).add(0,0.3,0),EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
        stand.setCustomName(ChatColor.RED + "龙息");
        w.playSound(stand.getEyeLocation(),Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
        for(int i = 0;i < 10;i ++ ){
            Arrow arrow = w.spawnArrow(stand.getEyeLocation(),shootVec,1,20);
            arrow.setTicksLived(1200);
            arrow.setDamage(6);
            arrow.setFireTicks(1200);
            arrow.setShooter(stand);
        }
        BukkitRunnable remove = new BukkitRunnable() {
            @Override
            public void run() {
                stand.remove();
            }
        };
        remove.runTaskLater(plugin,20L);
    }

    public void playerBall(Player player) {
        World w = player.getWorld();
        Location shootLoc = player.getEyeLocation();
        Vector upVec = new Vector(0, 1, 0);
        for (int i = 0; i < 10; i++) {
            w.playSound(shootLoc, Sound.BLOCK_SLIME_BLOCK_PLACE, 2, 1);
            double xSpread = random.nextDouble() - random.nextDouble();
            double zSpread = random.nextDouble() - random.nextDouble();
            Vector spread = new Vector(xSpread, 0, zSpread);
            Vector shootVec = (upVec.clone().add(spread.clone().multiply(1.5))).normalize().multiply(0.4);
            shootVec.add(shootLoc.getDirection().multiply(2));
            shootSlowBall(player, new ItemStack(Material.SLIME_BALL), shootLoc, shootVec, 20);
        }
    }
    public void playerArrow(Player player) {
        World w = player.getWorld();
        Location shootLoc = player.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        for (int i = 0; i < 10; i++) {
            w.playSound(shootLoc, Sound.ITEM_CROSSBOW_SHOOT, 2, 1);
            Arrow arrow = w.spawnArrow(shootLoc, shootVec, 1, 50);
            arrow.setDamage(30);
            arrow.setTicksLived(1200);
        }
    }
    public void playerAnvil(Player player) {
        World w = player.getWorld();
        Location shootLoc = player.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        w.playSound(shootLoc, Sound.ENTITY_IRON_GOLEM_HURT, 2, 1);
        BlockData data = Bukkit.createBlockData(Material.ANVIL);
        FallingBlock anvil = w.spawnFallingBlock(shootLoc, data);
        anvil.setDropItem(false);
        anvil.setVelocity(shootVec.multiply(2).clone());
        BukkitRunnable hit = new BukkitRunnable() {

            @Override
            public void run() {
                if (anvil.isDead() || anvil.isOnGround()) {
                    w.spawnParticle(Particle.BLOCK_CRACK, anvil.getLocation(), 20, 0, 0, 0, 0.4, data);
                    this.cancel();
                    anvil.remove();
                    w.getBlockAt(anvil.getLocation()).setType(Material.AIR);
                    return;
                }
                List<Entity> nearby = anvil.getNearbyEntities(2, 2, 2);
                for (Entity e : nearby) {
                    if (e == player) continue;
                    if (e instanceof Mob) {
                        ((Mob) e).damage(40);
                        w.playSound(((Mob) e).getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 2, 0.7f);
                        ((Mob)e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                        knockBack(e, anvil.getLocation(), 2);
                        anvil.remove();
                    }
                }
            }
        };
        hit.runTaskTimer(plugin, 0L, 1L);
    }
    public void playerDash(Player player) {
        World w = player.getWorld();
        w.playSound(player.getLocation(), Sound.ENTITY_COW_HURT, 2, 1);
        Vector dashVec = player.getEyeLocation().getDirection().clone();
        Vector finalVec = new Vector(dashVec.getX(), 0, dashVec.getZ());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20,5));
        BukkitRunnable dash = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (player.isDead()) {
                    this.cancel();
                    return;
                }
                if (count >= 3) {
                    this.cancel();
                    return;
                }
                count += 1;
                player.setVelocity(finalVec.multiply(1.5));
                List<Entity> nearby = player.getNearbyEntities(2, 2, 2);
                for (Entity e : nearby) {
                    if (e instanceof Mob) {
                        ((Mob) e).damage(30);
                        player.setVelocity(new Vector(0, 0, 0));
                        this.cancel();
                    }
                }
            }
        };
        dash.runTaskTimer(plugin, 0L, 5L);
    }
    public void playerMagic(LivingEntity entity){
        World w = entity.getWorld();
        Location shootLoc = entity.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        w.playSound(shootLoc,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        for (int i = 0; i < 10; i++) {
            Arrow arrow = w.spawnArrow(shootLoc, shootVec, 2, 12);
            arrow.setDamage(10);
            arrow.setTicksLived(1200);
            arrow.setShooter(entity);
            arrow.setColor(Color.PURPLE);
            arrow.setCustomName(ChatColor.LIGHT_PURPLE + "巫妖王的法术");
        }
    }
    public void playerEarthShake(Player player){
        World w = player.getWorld();
        BukkitRunnable count = new BukkitRunnable() {
            int num = 0;
            @Override
            public void run() {
                if(num > 3 || player.isDead()){
                    this.cancel();
                    return;
                }
                if(num < 2){
                    w.playSound(player.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(player.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(player.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                    w.playSound(player.getLocation(),Sound.BLOCK_STONE_BREAK,3,1);
                }
                if(num == 2) {
                    List<Entity>nearby = player.getNearbyEntities(20,20,20);
                    for(Entity e : nearby){
                        if(e instanceof Mob){
                            if(e.isOnGround()){
                                ((Mob) e).damage(40);
                            }
                        }
                    }
                    w.spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 50, 10, 0, 10);
                    w.playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
                }
                num += 1;
            }
        };
        count.runTaskTimer(plugin,0L,20L);
    }
    public void playerDragonShout(Player player){
        World w = player.getWorld();
        Location shootLoc = player.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        ArmorStand stand = (ArmorStand) w.spawnEntity(player.getLocation().add(shootVec.clone()).add(0,0.3,0),EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
        stand.setCustomName(ChatColor.RED + "龙息");
        w.playSound(stand.getEyeLocation(),Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
        for(int i = 0;i < 10;i ++ ){
            Arrow arrow = w.spawnArrow(stand.getEyeLocation(),shootVec,1,20);
            arrow.setTicksLived(1200);
            arrow.setDamage(20);
            arrow.setFireTicks(1200);
            arrow.setShooter(stand);
        }
        BukkitRunnable remove = new BukkitRunnable() {
            @Override
            public void run() {
                stand.remove();
            }
        };
        remove.runTaskLater(plugin,20L);
    }
    public void playerBoost(Player player){
        World w = player.getWorld();
        w.playSound(player.getEyeLocation(),Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED,1,1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,200,2));
    }

    public void lightning(Location loc,Player owner,double amplifier){
        World w = loc.getWorld();
        double xSpread = random.nextInt(15) - random.nextInt(15);
        double zSpread = random.nextInt(15) - random.nextInt(15);
        Location lightningLoc = loc.clone().add(xSpread,0,zSpread);
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(lightningLoc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(50);
        cloud.setColor(Color.WHITE);
        cloud.setRadius(3);
        owner.sendTitle(ChatColor.AQUA + "!小心闪电!"," ",0,20,10);
        BukkitRunnable strike = new BukkitRunnable() {
            @Override
            public void run() {
                w.strikeLightningEffect(lightningLoc);
                for(Entity e :w.getNearbyEntities(lightningLoc,3,3,3)){
                    if(e instanceof LivingEntity){
                        if(e instanceof Player){
                            ((Player) e).damage(Math.min(15,10 * amplifier));
                            e.setFireTicks(100);
                        }else {
                            ((LivingEntity) e).damage(10 * amplifier);
                            e.setFireTicks(200);
                        }
                    }else if(e instanceof Item){
                        e.remove();
                    }
                }
                cloud.remove();
            }
        };
        strike.runTaskLater(plugin,50L);
    }
    public void trap(Location loc,Player owner,double amplifier){
        World w = loc.getWorld();
        double xSpread = random.nextInt(10) - random.nextInt(10);
        double zSpread = random.nextInt(10) - random.nextInt(10);
        Location trapLoc = loc.clone().add(xSpread,0,zSpread);
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(trapLoc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(50);
        cloud.setColor(Color.BLACK);
        cloud.setRadius(5);
        owner.sendTitle(ChatColor.GRAY + "!小心陷阱!"," ",0,20,10);
        BukkitRunnable strike = new BukkitRunnable() {
            @Override
            public void run() {
                for(int x = -3;x <= 3;x ++){
                    for(int z = -3;z <= 3;z ++) {
                        Location fangLoc = trapLoc.clone().add(x,0,z);
                        w.spawnEntity(fangLoc,EntityType.EVOKER_FANGS);
                    }
                }
                for(Entity e :w.getNearbyEntities(trapLoc,4,4,4)){
                    if(e instanceof LivingEntity){
                        if(e instanceof Player){
                            ((Player) e).damage(Math.min(15,10 * amplifier));
                        }else {
                            ((LivingEntity) e).damage(10 * amplifier);
                        }
                    }
                }
                cloud.remove();
            }
        };
        strike.runTaskLater(plugin,50L);
    }
    public void sandstorm(Location loc,Player owner,double amplifier){
        World w = loc.getWorld();
        double xSpread = random.nextInt(5) - random.nextInt(5);
        double zSpread = random.nextInt(5) - random.nextInt(5);
        Location trapLoc = loc.clone().add(xSpread,0,zSpread);
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(trapLoc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(100);
        cloud.setColor(Color.ORANGE);
        cloud.setRadius(10);
        owner.sendTitle(ChatColor.YELLOW + "!小心沙尘暴!"," ",0,20,10);
        BukkitRunnable strike = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.BLOCK_DUST,trapLoc,200,5,5,5,Bukkit.createBlockData(Material.SAND));
                w.playSound(trapLoc,Sound.ENTITY_BAT_TAKEOFF,1.5f,1);
                suck(cloud,trapLoc,10,-4);
                for(Entity e :w.getNearbyEntities(trapLoc,10,10,10)){
                    if(e instanceof LivingEntity){
                        if(e instanceof Player){
                            ((Player) e).damage(Math.min(5,2 * amplifier));
                            ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20,0));
                        }else {
                            ((LivingEntity) e).damage(5 * amplifier);
                        }
                    }
                }
                cloud.remove();
            }
        };
        strike.runTaskLater(plugin,100L);
    }
    public void lavaBlast(Location loc,Player owner,double amplifier){
        World w = loc.getWorld();
        double xSpread = random.nextInt(10) - random.nextInt(10);
        double zSpread = random.nextInt(10) - random.nextInt(10);
        Location trapLoc = loc.clone().add(xSpread,0,zSpread);
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(trapLoc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(50);
        cloud.setParticle(Particle.SMOKE_LARGE);
        cloud.setRadius(5);
        owner.sendTitle(ChatColor.GOLD + "!小心岩浆!"," ",0,20,10);
        BukkitRunnable strike = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.EXPLOSION_HUGE,trapLoc,1);
                w.playSound(trapLoc,Sound.ENTITY_GENERIC_EXPLODE,1,1);
                areaDOT(cloud,trapLoc,100L,5,amplifier * 2,Particle.FLAME);
                cloud.remove();
            }
        };
        strike.runTaskLater(plugin,50L);
    }
    public void mapAbility(Location loc,Player owner,int type,double amplifier){
        switch (type){
            case 0:
                lightning(loc,owner,amplifier);
                break;
            case 1:
                trap(loc,owner,amplifier);
                break;
            case 2:
                sandstorm(loc,owner,amplifier);
                break;
            case 3:
                lavaBlast(loc,owner,amplifier);
        }
    }
}
