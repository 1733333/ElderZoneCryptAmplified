package Pool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MaterialPool {
    private static MaterialPool instance = new MaterialPool();
    private MaterialPool(){}
    public static MaterialPool getInstance() {
        return instance;
    }
    Random random = new Random();
    ItemStack[]materials = new ItemStack[]{
            new ItemStack(Material.WHITE_WOOL,16),      //羊毛 ID：0      N
            new ItemStack(Material.BLUE_ORCHID,4),     //兰花 ID：1    N
            new ItemStack(Material.BLUE_ICE,8),        //蓝冰 ID：2      R
            new ItemStack(Material.SAND,16),            //沙子 ID：3      N
            new ItemStack(Material.SOUL_SAND,4),        //灵魂沙 ID：4      SR
            new ItemStack(Material.ICE,1),             //水桶 ID：5      R
            new ItemStack(Material.LAVA_BUCKET,1),     //岩浆桶 ID：6    R
            new ItemStack(Material.PRISMARINE,4),            //海晶石 ID：7      N
            new ItemStack(Material.BLACKSTONE,16),         //黑石 ID：8   N
            new ItemStack(Material.OBSIDIAN,8),        //黑曜石 ID：9    SSR
            new ItemStack(Material.IRON_INGOT,8),      //铁锭 ID：10   R
            new ItemStack(Material.GOLD_INGOT,8),      //金锭 ID：11   SR
            new ItemStack(Material.DIAMOND,4),         //钻石 ID：12    SSR
            new ItemStack(Material.EMERALD,8),         //绿宝石 ID：13   SR
            new ItemStack(Material.ANDESITE,16),        //安山岩
            new ItemStack(Material.DIORITE,16),         //闪长岩
            new ItemStack(Material.GRANITE,16),         //花岗岩
            new ItemStack(Material.END_STONE,8),         //末地石
            new ItemStack(Material.COAL,8),             //煤炭
            new ItemStack(Material.LAPIS_LAZULI,8),          //青金石
            new ItemStack(Material.REDSTONE,8),             //红石
            new ItemStack(Material.COPPER_INGOT,8),           //铜锭
            new ItemStack(Material.AMETHYST_SHARD,8),            //紫水晶碎片
            new ItemStack(Material.AZALEA_LEAVES,4),             //杜鹃树叶
            new ItemStack(Material.FLOWERING_AZALEA_LEAVES,4),       //开花杜鹃树叶
            new ItemStack(Material.FLOWERING_AZALEA,4),        //杜鹃花
            new ItemStack(Material.AZALEA,4),                  //杜鹃
            new ItemStack(Material.BIG_DRIPLEAF,2),             //垂滴液
            new ItemStack(Material.QUARTZ,8),                   //石英
            new ItemStack(Material.COBBLESTONE,16),              //圆石
            new ItemStack(Material.ROTTEN_FLESH,4),                 //腐肉
            new ItemStack(Material.BONE,4),                         //骨头
            new ItemStack(Material.GUNPOWDER,4),                    //火药
            new ItemStack(Material.SPIDER_EYE,4),                   //蜘蛛眼
            new ItemStack(Material.STRING,4),                   //线
            new ItemStack(Material.HONEYCOMB,4),                   //蜜脾
            new ItemStack(Material.GLOWSTONE,4),                   //萤石
            new ItemStack(Material.SEA_LANTERN,4),                   //海晶灯
    };
    ItemStack[]betterMaterials = new ItemStack[]{
            new ItemStack(Material.VILLAGER_SPAWN_EGG),        //村民刷怪蛋 ID：15       SR
            new ItemStack(Material.PIG_SPAWN_EGG),             //猪刷怪蛋 ID：16        R
            new ItemStack(Material.COW_SPAWN_EGG),             //牛刷怪蛋 ID：17        R
            new ItemStack(Material.SHEEP_SPAWN_EGG),           //羊刷怪蛋 ID：18         R
            new ItemStack(Material.CHICKEN_SPAWN_EGG),         //鸡刷怪蛋 ID：19         R
            sweepTicket(),                                     //扫荡券 ID：20      UR
            new ItemStack(Material.OAK_SAPLING, 4),      //橡树树苗
            new ItemStack(Material.SPRUCE_SAPLING, 4),     //云杉树苗
            new ItemStack(Material.BIRCH_SAPLING, 4),     //桦树树苗
            new ItemStack(Material.DARK_OAK_SAPLING, 4),     //深色橡树树苗
            new ItemStack(Material.ACACIA_SAPLING, 4),     //金合欢树苗
            new ItemStack(Material.JUNGLE_SAPLING, 4),     //丛林树苗
            new ItemStack(Material.BAMBOO, 4),               //竹子
            new ItemStack(Material.SUGAR_CANE, 4),               //甘蔗
            new ItemStack(Material.WHEAT_SEEDS, 4),               //小麦种子
            new ItemStack(Material.NETHERITE_INGOT,2),       //下届合金锭
            new ItemStack(Material.EXPERIENCE_BOTTLE,16),     //经验瓶
            new ItemStack(Material.POINTED_DRIPSTONE,4),        //钟乳石
            new ItemStack(Material.CARROT,4),                   //胡萝卜
            new ItemStack(Material.POTATO,4),                   //土豆
            new ItemStack(Material.CHORUS_FLOWER,4),            //紫颂花
            new ItemStack(Material.DIRT,8),                     //泥土
            new ItemStack(Material.MYCELIUM,8),                 //菌丝
            new ItemStack(Material.GRAVEL,8),                 //沙砾
            new ItemStack(Material.ENDER_PEARL,4),            //末影珍珠
            new ItemStack(Material.SLIME_BALL,8),             //粘液球
            new ItemStack(Material.BEE_SPAWN_EGG),                  //蜜蜂刷怪蛋
            new ItemStack(Material.GRASS_BLOCK,8),                   //草方块
    };

    float[] materialChancePool = new float[]{
            0.01f,                            //羊毛 ID：0      N
            0.0025f,                          //兰花 ID：1    N
            0.005f,                          //蓝冰 ID：2      R
            0.01f,                          //沙子 ID：3      N
            0.0025f,                         //菌丝 ID：4      SR
            0.00125f,                             //水桶 ID：5      SR
            0.00125f,                             //岩浆桶 ID：6    SR
            0.01f,                           //泥土 ID：7      N
            0.01f,                            //黑石 ID：8   N
            0.00125f,                         //黑曜石 ID：9    SSR
            0.005f,                           //铁锭 ID：10   R
            0.0025f,                           //金锭 ID：11   SR
            0.00125f,                        //钻石 ID：12    SSR
            0.0025f,                        //绿宝石 ID：13   SR
            0.01f,                             //安山岩
            0.01f,                             //闪长岩
            0.01f,                              //玄武岩
            0.01f,                              //沙砾
            0.01f,                        //煤炭
            0.005f,                       //青金石
            0.005f,                       //红石
            0.005f,                       //铜锭
            0.005f,                       //紫水晶碎片
            0.0025f,                       //杜鹃树叶
            0.0025f,                       //开花杜鹃树叶
            0.0025f,                       //杜鹃花
            0.0025f,                       //杜鹃
            0.0025f,                       //垂滴液
            0.005f,                      //石英
            0.01f,                        //圆石
            0.005f,                        //腐肉
            0.0025f,                        //骨头
            0.005f,                        //火药
            0.0025f,                           //蜘蛛眼
            0.005f,                         //线
            0.0025f,                        //蜜脾
            0.0025f,                           //萤石
            0.0025f,                         //海晶灯
    };
    float[] betterMaterialChancePool = new float[]{
            0.0025f,                        //村民刷怪蛋 ID：18       SR
            0.005f,                        //猪刷怪蛋 ID：19         R
            0.005f,                        //牛刷怪蛋 ID：20         R
            0.005f,                        //羊刷怪蛋 ID：21         R
            0.005f,                        //鸡刷怪蛋 ID：22         R
            0.000625f,                        //扫荡券 ID：23      UR
            0.01f,                              //橡树树苗
            0.01f,                                //云杉树苗
            0.01f,                               //桦树树苗
            0.01f,                                  //深色橡树树苗
            0.01f,                                //金合欢树苗
            0.01f,                                //丛林树苗
            0.01f,                                  //竹子
            0.01f,                                  //甘蔗
            0.01f,                                  //小麦种子
            0.0025f,                                //下届合金锭
            0.01f,                                  //经验瓶
            0.005f,                                 //钟乳石
            0.01f,                                   //胡萝卜
            0.01f,                                   //土豆
            0.005f,                                 //紫颂花
            0.01f,                                 //泥土
            0.01f,                                 //菌丝
            0.01f,                               //沙砾
            0.0025f,                            //末影珍珠
            0.0025f,                            //粘液球
            0.005f,                             //蜜蜂刷怪蛋
            0.01f,                              //草方块
    };

    float[] materialChance = new float[materialChancePool.length];
    float[] betterMaterialChance = new float[betterMaterialChancePool.length];


    public void calculate(float[] chancePool, float[] chances) {
        float f = 0;
        for (int i = 0; i < chancePool.length; i++) {
            f = f + chancePool[i];
            chances[i] = f;
        }
        if (f != 1) {
            for (int i = 0; i < chances.length; i++) {
                chances[i] = chances[i] / f;
            }
        }
    }
    public int compare(float[] chances,float randFloat){
        for(int i = 0 ; i < chances.length ; i ++){
            if(randFloat< chances[i]){
                return i;
            }
        }
        return -1;
    }
    public ItemStack[] randomMaterial(int amount){
        List<ItemStack>itemStackList = new ArrayList<>();
        HashMap<Integer,Integer>materialAmount = new HashMap<>();
        calculate(materialChancePool,materialChance);
        for(int i = 0;i < amount;i++){
            float f = random.nextFloat();
            int result = compare(materialChance,f);
            if(result == -1)continue;
            int amountM = materialAmount.getOrDefault(result,-1);
            ItemStack material = materials[result];
            if(amountM == -1){
                materialAmount.put(result,material.getAmount());
            }else {
                materialAmount.put(result,amountM + material.getAmount());
            }
        }
        for(int j = 0;j < materials.length;j++){
            int recordedMaterialAmount = materialAmount.getOrDefault(j,-1);
            if(recordedMaterialAmount == -1)continue;
            ItemStack recordedMaterial = materials[j].clone();
            for (int i = 0;i <= recordedMaterialAmount/64;i++){
                int finalAmount = Math.min(recordedMaterialAmount - 64 * i,64);
                if(finalAmount == 0)continue;
                ItemStack itemStack = new ItemStack(recordedMaterial.getType(),finalAmount);
                itemStackList.add(itemStack);
            }
        }
        return itemStackList.toArray(new ItemStack[0]);
    }

    public ItemStack[] betterMaterials(int amount) {
        List<ItemStack>itemStackList = new ArrayList<>();
        HashMap<Integer,Integer>materialAmount = new HashMap<>();
        calculate(betterMaterialChancePool,betterMaterialChance);
        for(int i = 0;i < amount;i++){
            float f = random.nextFloat();
            int result = compare(betterMaterialChance,f);
            if(result == -1)continue;
            int amountM = materialAmount.getOrDefault(result,-1);
            ItemStack material = betterMaterials[result];
            if(amountM == -1){
                materialAmount.put(result,material.getAmount());
            }else {
                materialAmount.put(result,amountM + material.getAmount());
            }
        }
        for(int j = 0;j < betterMaterials.length;j++){
            int recordedMaterialAmount = materialAmount.getOrDefault(j,-1);
            if(recordedMaterialAmount == -1)continue;
            ItemStack recordedMaterial = betterMaterials[j].clone();
            for (int i = 0;i <= recordedMaterialAmount/64;i++){
                int finalAmount = Math.min(recordedMaterialAmount - 64 * i,64);
                if(finalAmount == 0)continue;
                recordedMaterial.setAmount(finalAmount);
                itemStackList.add(recordedMaterial);
            }
        }
        return itemStackList.toArray(new ItemStack[0]);
    }

    public ItemStack sweepTicket(){
        ItemStack ticket = new ItemStack(Material.PAPER);
        ItemMeta ticketMeta = ticket.getItemMeta();
        ticketMeta.setDisplayName(ChatColor.GOLD + "地牢扫荡券");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "SweepTicket");
        lore.add(ChatColor.WHITE + "护肝大使");
        lore.add(ChatColor.GRAY + "获得丰厚奖励");
        ticketMeta.setLore(lore);
        ticket.setItemMeta(ticketMeta);
        return ticket;
    }
}
