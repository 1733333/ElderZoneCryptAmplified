package Pool;

import UniversalMethod.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class WeaponPool {
    private static WeaponPool instance = new WeaponPool();
    private WeaponPool(){}
    public static WeaponPool getInstance() {
        return instance;
    }
    Random random = new Random();
    Tools t = Tools.getInstance();
    HashMap<String,Integer>weaponID = new HashMap<>();
    HashMap<Integer,ArrayList<Integer>>formula = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>>deForm = new HashMap<>();
    ArrayList<String>formulaOutput = new ArrayList<>();

    ItemStack[]weapons = {
            sword(),                                 //0
            axe(),                                   //1
            bow(),                                   //2
            crossBow(),                              //3
            stick(),                                 //4
            magicBook(),                             //5
            shotCrossbow(),                          //6
            boomerAxe(),                             //7
            shieldAxe(),                             //8
            hammer(),                                //9
            machineCrossbow(),                       //10
            fastBow(),                               //11
            boostedBook(),                           //12
            thickBook(),                             //13
            grenadeStick(),                          //14
            machineShotBow(),                        //15
            tamaKiri(),                              //16
            flail(),                                 //17
            enchantedSword(),                        //18
            OPSword(),                               //19
            masterSword(),                           //20
            doubleBow(),                             //21
            tripleCrossbow(),                        //22
            meleeBow(),                              //23
            iceBow(),                                //24
            enchantedAxe(),                          //25
            assassinAxe(),                           //26
            diceHammer(),                            //27
            randomBook(),                            //28
            boostedEnchantedSword(),                 //29
            OPSwordKiwami(),                         //30
            sharpSword(),                            //31
            monarchSword(),                          //32
            monarchSwordI(),                         //33
            monarchSwordII(),                        //34
            monarchSwordIII(),                       //35
            smokeOni(),                              //36
            reflexAxe(),                             //37
            shield(),                                //38
            chopAxe(),                               //39
            fireShield(),                            //40
            waterShield(),                           //41
            lavaShield(),                            //42
            iceShield(),                             //43
            AShield(),                               //44
            rainBow(),                               //45
            smokeSword(),                            //46

            monsterStaff(),
            guitarAxe(),
            deathStaff(),
            kelp(),
            bread(),
            chicken(),
            goldenZweiHander(),
            meteor(),
            VillarPerosa(),
            sentryHelm(),
            rawCola(),
            sniperRifle(),
    };

    public void registerWeapons(){
        for(int i = 0;i<weapons.length;i++){
            ItemStack weapon = weapons[i];
            ItemMeta weaponMeta = weapon.getItemMeta();
            if(weaponMeta.hasDisplayName()){
                String name = weaponMeta.getDisplayName();
                weaponID.put(name,i);
            }else {
                weaponID.put(weapon.getType().name(),i);
            }
        }
    }

    public void registerFormula() {
        addWeaponFormula(0,16);
        addWeaponFormula(16,19);
        addWeaponFormula(19,30);
        addWeaponFormula(30,20);

        addWeaponFormula(0,31);
        addWeaponFormula(31,18);
        addWeaponFormula(18,29);
        addWeaponFormula(29,20);

        addWeaponFormula(0,32);
        addWeaponFormula(32,33);
        addWeaponFormula(33,34);
        addWeaponFormula(34,35);

        addWeaponFormula(1,7);
        addWeaponFormula(7,25);
        addWeaponFormula(25,26);
        addWeaponFormula(26,39);

        addWeaponFormula(1,8);
        addWeaponFormula(8,37);
        addWeaponFormula(37,30);
        addWeaponFormula(30,39);

        addWeaponFormula(3,22);
        addWeaponFormula(22,6);
        addWeaponFormula(6,10);
        addWeaponFormula(10,15);

        addWeaponFormula(2,11);
        addWeaponFormula(2,21);
        addWeaponFormula(2,23);
        addWeaponFormula(11,24);
        addWeaponFormula(21,45);
        addWeaponFormula(23,36);
        addWeaponFormula(36,46);
        addWeaponFormula(46,36);


        addWeaponFormula(4,9);
        addWeaponFormula(9,17);
        addWeaponFormula(17,27);
        addWeaponFormula(17,14);
        addWeaponFormula(27,20);
        addWeaponFormula(14,20);

        addWeaponFormula(5,13);
        addWeaponFormula(13,12);
        addWeaponFormula(12,28);

        addWeaponFormula(38,40);
        addWeaponFormula(38,41);
        addWeaponFormula(40,42);
        addWeaponFormula(41,43);
        addWeaponFormula(42,44);
        addWeaponFormula(43,44);
    }
    public void addWeaponFormula(int ingredient,int result){
        ArrayList<Integer>ingredientList = formula.getOrDefault(result,new ArrayList<>());
        ingredientList.add(ingredient);
        formula.put(result,ingredientList);
        ArrayList<Integer>formulaList = deForm.getOrDefault(ingredient,new ArrayList<>());
        formulaList.add(result);
        deForm.put(ingredient,formulaList);
        ItemStack ingredientStack = weapons[ingredient];
        ItemStack resultStack = weapons[result];
        String ingredientName = ingredientStack.getItemMeta().getDisplayName();
        String resultName = resultStack.getItemMeta().getDisplayName();
        String formula = ingredientName + ChatColor.RESET + " ==> " + resultName;
        formulaOutput.add(formula);
    }

    public String[] getFormulaOutput() {
        return formulaOutput.toArray(new String[]{});
    }
    public ItemStack[] getWeapons() {
        return weapons;
    }

    public String getName(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta.hasDisplayName()){
            return itemMeta.getDisplayName();
        }else {
            return item.getType().name();
        }
    }

    public ArrayList<Integer> craftItem(ItemStack ingredient){
        String ingredientName = getName(ingredient);
        int ingredientID = weaponID.getOrDefault(ingredientName,-1);
        if(ingredientID == -1)return new ArrayList<>();
        return formula.getOrDefault(ingredientID,new ArrayList<>());
    }

    public ItemStack[] breakItem(ItemStack itemStack){
        String name = getName(itemStack);
        int itemID = weaponID.getOrDefault(name,-1);
        if(itemID == -1)return new ItemStack[]{};
        ArrayList<Integer>formula = deForm.getOrDefault(itemID,new ArrayList<>());
        if(formula.size()==0)return new ItemStack[]{};
        ItemStack[] items = new ItemStack[formula.size()];
        ItemStack[] finalItems = new ItemStack[formula.size()];
        for(int i = 0;i < formula.size();i ++){
            items[i] = weapons[formula.get(i)];
            ItemStack itemStackClone = items[i].clone();
            ItemMeta itemMeta = itemStackClone.getItemMeta();
            String itemName = getName(itemStackClone);
            String newName =ChatColor.AQUA + "将 " + name + ChatColor.RESET + ChatColor.AQUA +  " 进化为：" + itemName;
            itemMeta.setDisplayName(newName);
            itemStackClone.setItemMeta(itemMeta);
            finalItems[i] = itemStackClone;
        }
        return finalItems;
    }
    public void shuffle(int[] numbers) {
        for (int i = numbers.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            int num = numbers[randomNum];
            numbers[randomNum] = numbers[i];
            numbers[i] = num;
        }
    }
    public ItemStack[]randomMaterial(int amount){
        if(amount > 6){
            amount = 6;
        }
        ItemStack[]randMaterial = new ItemStack[amount];
        ItemStack[]finalRandMaterial = new ItemStack[amount];
        int[]IDs = new int[amount];
        int[]allNum = new int[]{0,1,2,3,4,5,38};
        shuffle(allNum);
        System.arraycopy(allNum, 0, IDs, 0, amount);
        for (int i = 0;i < amount;i ++){
            randMaterial[i] = weapons[IDs[i]];
            ItemStack clone = randMaterial[i].clone();
            ItemMeta meta = clone.getItemMeta();
            String name = getName(clone);
            String newName =ChatColor.AQUA + "获得另一把：" + name;
            meta.setDisplayName(newName);
            clone.setItemMeta(meta);
            finalRandMaterial[i] = clone;
        }
        return finalRandMaterial;
    }

    public ItemStack sword(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.WHITE + "长剑");
        String[] lore = {
                ChatColor.GRAY  + "Sword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 格挡",
                ChatColor.WHITE + "格挡可以在一段时间内抵消" + ChatColor.YELLOW + "一次近战或弓箭伤害",
                ChatColor.YELLOW + "成功格挡" + ChatColor.WHITE + " : 清除负面效果",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把护手剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以兼顾攻击和格挡",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack axe(){
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),9, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-3.1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),2, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        axeMeta.addAttributeModifier(attribute1,modifier1);
        axeMeta.addAttributeModifier(attribute2,modifier2);
        axeMeta.addAttributeModifier(attribute3,modifier3);
        axeMeta.addAttributeModifier(attribute4,modifier4);
        axeMeta.setDisplayName(ChatColor.WHITE + "战斧");
        String[] lore = {
                ChatColor.GRAY  + "Axe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 战吼",
                ChatColor.WHITE + "战吼可以" + ChatColor.YELLOW + "造成范围伤害和击退效果",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把很普通的斧头",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "但握在手中力量会不由自主的涌现",
        };
        axe.setItemMeta(axeMeta);
        t.addLore(axe,lore);
        return axe;
    }
    public ItemStack bow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "长弓");
        String[] lore = {
                ChatColor.GRAY + "Bow",
                ChatColor.YELLOW + "拉满弓射击" + ChatColor.WHITE + " : 造成额外伤害",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Shift" + ChatColor.WHITE + " : 突进",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一张很普通的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "威力还说得过去",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack meleeBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "近战弓");
        bowMeta.addEnchant(Enchantment.DAMAGE_ALL,10,true);
        String[] lore = {
                ChatColor.GRAY + "MeleeBow",
                ChatColor.WHITE + "射击近距离的怪物" + ChatColor.YELLOW + "会造成额外伤害并击退",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一张非常重的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "重到拿来砸人都没问题",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack fastBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "速射弓");
        String[] lore = {
                ChatColor.GRAY + "FastBow",
                ChatColor.WHITE + "不用拉满弓也可以" + ChatColor.YELLOW + "满威力射击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "弓的材料经过了轻量化处理",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "不用拉满威力也不会减弱",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "但扩散比一般的弓要差",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack crossBow(){
        ItemStack crossBow = new ItemStack(Material.CROSSBOW);
        ItemMeta crossBowMeta = crossBow.getItemMeta();
        crossBowMeta.setDisplayName(ChatColor.WHITE + "轻弩");
        crossBowMeta.addEnchant(Enchantment.QUICK_CHARGE,1,false);
        crossBowMeta.addEnchant(Enchantment.PIERCING,1,false);
        String[] lore = {
                ChatColor.GRAY  + "CrossBow",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Shift" + ChatColor.WHITE + " : 突进",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "标准的十字弩",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "其独特的结构可以加速装填",
        };
        crossBow.setItemMeta(crossBowMeta);
        t.addLore(crossBow,lore);
        return crossBow;
    }
    public ItemStack stick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta rodMeta = stick.getItemMeta();
        rodMeta.setDisplayName(ChatColor.WHITE + "林昆");
        String[] lore = {
                ChatColor.GRAY + "Stick",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 极小概率一击必杀",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "随手捡的一根木棍",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "只有GigaChad能够真正的驾驭它",
        };
        stick.setItemMeta(rodMeta);
        t.addLore(stick, lore);
        return stick;
    }
    public ItemStack magicBook(){
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "魔法书");
        String[] lore = {
                ChatColor.GRAY + "MagicBook",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放远程攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一本魔法书",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以使用基本魔法",
        };
        book.setItemMeta(bookMeta);
        t.addLore(book,lore);
        return book;
    }
    public ItemStack shield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.setDisplayName(ChatColor.WHITE + "盾");
        String[] lore = {
                ChatColor.GRAY  + "Shield",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一面比较结实的盾",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以抵挡攻击,而且能勉强当作武器",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack fireShield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),15, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.addEnchant(Enchantment.FIRE_ASPECT,2,true);
        shieldItemMeta.setDisplayName(ChatColor.GOLD + "火焰盾");
        String[] lore = {
                ChatColor.GRAY  + "FireShield",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Shift" + ChatColor.WHITE + " : 盾牌冲撞",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "在盾上撒上燃料之后点燃",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "就可以得到这面火焰盾",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack waterShield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),15, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.setDisplayName(ChatColor.AQUA + "水盾");
        String[] lore = {
                ChatColor.GRAY  + "WaterShield",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "用水做成的盾,这怎么可能?",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "击退敌人的能力更强了",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack iceShield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),20, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.setDisplayName(ChatColor.AQUA + "冰盾");
        String[] lore = {
                ChatColor.GRAY  + "IceShield",
                ChatColor.YELLOW + "挡住攻击" + ChatColor.WHITE + " : 释放冰刺",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                ChatColor.WHITE + "如果被攻击的敌人处于寒冷状态",
                ChatColor.WHITE + "伤害会" + ChatColor.YELLOW + "翻倍",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "在极寒之地找到的,被冻起来的水盾",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "非常的Cool",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack lavaShield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),20, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
        shieldItemMeta.setDisplayName(ChatColor.GOLD + "熔岩盾");
        String[] lore = {
                ChatColor.GRAY  + "LavaShield",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Shift" + ChatColor.WHITE + " : 盾牌冲撞",
                ChatColor.YELLOW + "挡住攻击" + ChatColor.WHITE + " : 释放火焰",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "简单粗暴地把熔岩浇在盾上",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "收到冲击还会溅出火花",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack AShield(){
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta shieldItemMeta = shield.getItemMeta();
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),25, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        shieldItemMeta.addAttributeModifier(attribute2,modifier2);
        shieldItemMeta.addAttributeModifier(attribute3,modifier3);
        shieldItemMeta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
        shieldItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "A盾");
        String[] lore = {
                ChatColor.GRAY  + "AShield",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 盾击",
                ChatColor.WHITE + "盾击可以击退敌人",
                ChatColor.WHITE + "如果敌人在被击退的途中撞到其他敌人或墙壁",
                ChatColor.WHITE + "就会造成" + ChatColor.YELLOW + "额外伤害",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 依次释放以下能力",
                ChatColor.AQUA + "水 :" + ChatColor.WHITE + "发射一横排水属性远程攻击",
                ChatColor.RED + "火 :" + ChatColor.WHITE + "散射火属性远程攻击",
                ChatColor.GOLD + "土 :" + ChatColor.WHITE + "在正前方释放土墙,相当于一次特大威力盾击",
                ChatColor.GREEN + "气 :" + ChatColor.WHITE + "对范围内所有敌人释放一次大威力盾击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "A stands for Avatar",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "拥有四种元素属性的盾",
        };
        shield.setItemMeta(shieldItemMeta);
        t.addLore(shield,lore);
        return shield;
    }
    public ItemStack shotCrossbow(){
        ItemStack bow = new ItemStack(Material.CROSSBOW);
        ItemMeta bowItemMeta = bow.getItemMeta();
        bowItemMeta.setDisplayName(ChatColor.WHITE + "散射弩");
        bowItemMeta.addEnchant(Enchantment.QUICK_CHARGE,2,false);
        String[] lore = {
                ChatColor.GRAY + "ShotCrossbow",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 散射箭矢",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一次性射出更多箭的弩",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "而且装填速度也得到了提升",
        };
        bow.setItemMeta(bowItemMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack boomerAxe(){
        ItemStack stick = new ItemStack(Material.IRON_AXE);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName(ChatColor.WHITE + "回旋斧头");
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),12, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-3.2, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        stickMeta.addAttributeModifier(attribute1,modifier1);
        stickMeta.addAttributeModifier(attribute2,modifier2);
        stickMeta.addAttributeModifier(attribute3,modifier3);
        stickMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "BoomerAxe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 投掷斧头",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 重置投掷的冷却时间",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "当你恰到好处的在斧头柄上加了一段木棍",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "就得到了回旋斧头",
        };
        stick.setItemMeta(stickMeta);
        t.addLore(stick,lore);
        return stick;
    }
    public ItemStack hammer(){
        ItemStack hammer = new ItemStack(Material.STONE_SHOVEL);
        ItemMeta hammerMeta = hammer.getItemMeta();
        Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute.name(),-3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        hammerMeta.addAttributeModifier(attribute,modifier);
        hammerMeta.addAttributeModifier(attribute1,modifier1);
        hammerMeta.setDisplayName(ChatColor.GRAY + "锤子");
        String[] lore = {
                ChatColor.GRAY + "Hammer",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 概率击晕敌人",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把石头做的锤子",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "看起来像铲子",
        };
        hammer.setItemMeta(hammerMeta);
        t.addLore(hammer,lore);
        return hammer;
    }
    public ItemStack machineCrossbow(){
        ItemStack bow = new ItemStack(Material.CROSSBOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "机械弩");
        String[] lore = {
                ChatColor.GRAY + "MachineCrossbow",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 连续箭矢",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "加了复杂机械构造的弩",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以一次性射出更多箭矢",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "但由于其结构复杂,装填速度变慢了",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }

    public ItemStack boostedBook(){
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "强化魔法书");
        String[] lore = {
                ChatColor.GRAY + "BoostedBook",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 发射远程攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "魔法经过强化的魔法书",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以一次性发射更多远程攻击",
        };
        book.setItemMeta(bookMeta);
        t.addLore(book,lore);
        return book;
    }
    public ItemStack thickBook(){
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookItemMeta = book.getItemMeta();
        bookItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "厚重魔法书");
        Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        bookItemMeta.addAttributeModifier(attribute,modifier);
        String[] lore = {
                ChatColor.GRAY + "ThickBook",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 发射远程攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "加厚处理的魔法书",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以直接用来近战",
        };
        book.setItemMeta(bookItemMeta);
        t.addLore(book,lore);
        return book;
    }
    public ItemStack grenadeStick(){
        ItemStack hammer = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta hammerMeta = hammer.getItemMeta();
        hammerMeta.setDisplayName(ChatColor.GRAY + "哑弹棒");
        Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 25, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), attribute.name(), -2.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        hammerMeta.addAttributeModifier(attribute,modifier);
        hammerMeta.addAttributeModifier(attribute2,modifier2);
        String[] lore = {
                ChatColor.GRAY + "GrenadeStick",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 概率造成爆炸",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一根哑火的手雷",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "使用起来完全安全......吧",
        };
        hammer.setItemMeta(hammerMeta);
        t.addLore(hammer,lore);
        return hammer;
    }
    public ItemStack machineShotBow(){
        ItemStack bow = new ItemStack(Material.CROSSBOW);
        ItemMeta bowItemMeta = bow.getItemMeta();
        bowItemMeta.setDisplayName(ChatColor.GOLD + "机械散射弩");
        bowItemMeta.addEnchant(Enchantment.QUICK_CHARGE,1,false);
        String[] lore = {
                ChatColor.GRAY + "MachineShotBow",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 连发散射箭矢",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "在机械弩的机械装置上又安装了机械装置",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "提升了装填速度,增加了射出的箭矢数量",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "但射速降低了",
        };
        bow.setItemMeta(bowItemMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack tamaKiri(){
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.YELLOW + "玉切丸");
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL,10,true);
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE,1,true);
        String[] lore = {
                ChatColor.GRAY + "TamaKiri",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 格挡",
                ChatColor.WHITE + "格挡可以在一段时间内抵消" + ChatColor.YELLOW + "一次近战或弓箭伤害",
                ChatColor.YELLOW + "成功格挡" + ChatColor.WHITE + " : 清除远程攻击",
                ChatColor.WHITE + "并根据远程攻击的数量" + ChatColor.YELLOW + "获得力量,速度和抗性提升效果",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "以黄金点缀的,异常锋利的武士刀",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "锋利到可以切开子弹",
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack flail(){
        ItemStack hammer = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta hammerMeta = hammer.getItemMeta();
        Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute.name(),-3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        hammerMeta.addAttributeModifier(attribute,modifier);
        hammerMeta.addAttributeModifier(attribute1,modifier1);
        hammerMeta.setDisplayName(ChatColor.GRAY + "流星锤");
        String[] lore = {
                ChatColor.GRAY + "Flail",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 造成范围伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "用绳索拴在锤子的柄上",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "这样你就得到了一个流星锤",
        };
        hammer.setItemMeta(hammerMeta);
        t.addLore(hammer,lore);
        return hammer;
    }
    public ItemStack enchantedSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "附魔剑");
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY + "EnchantedSword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放远程攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "附魔之后的剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "不仅可以近战，也可以发射远程攻击",
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword, lore);
        return sword;
    }
    public ItemStack boostedEnchantedSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "断钢剑");
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 20, true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY + "BoostedEnchantedSword",
                ChatColor.YELLOW + "挥动武器" + ChatColor.WHITE + " : 释放剑气",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放远程攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "开了光的附魔剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以释放剑气",
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword, lore);
        return sword;
    }
    public ItemStack OPSword() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.GREEN + "源氏的龙神剑");
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 20, true);
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE,2,true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY + "OpSword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 清除远程攻击",
                ChatColor.YELLOW + "成功清除远程攻击时:",
                ChatColor.WHITE + "释放魔法",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "不知道为什么会出现在这里的剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "这是" + ChatColor.GREEN + "源" +
                        ChatColor.WHITE + "氏，这是他的大招" + ChatColor.GREEN + "劈"
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword, lore);
        return sword;
    }
    public ItemStack OPSwordKiwami() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.GREEN + "龙神剑·极");
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 30, true);
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE,3,true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY + "OpSwordKiwami",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 清除远程攻击",
                ChatColor.YELLOW + "成功清除远程攻击时:",
                ChatColor.WHITE + "释放魔法,并积攒数量",
                ChatColor.WHITE + "下一次近战攻击会根据积攒的数量增加伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "登峰造顶的龙神剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "已经将立体防御发挥到了极致",
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword, lore);
        return sword;
    }
    public ItemStack masterSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.AQUA + "大师剑");
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE,3,true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),30, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-2.4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordItemMeta.addAttributeModifier(attribute,modifier);
        swordItemMeta.addAttributeModifier(attribute2,modifier2);
        swordItemMeta.addAttributeModifier(attribute3,modifier3);
        String[] lore = {
                ChatColor.GRAY + "MasterSword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放远程剑气",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "你能拿到的最好的剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以发射更强力的远程攻击",
        };
        sword.setItemMeta(swordItemMeta);
        t.addLore(sword, lore);
        return sword;
    }
    public ItemStack doubleBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "双发弓");
        String[] lore = {
                ChatColor.GRAY + "DoubleBow",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 二连发",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Shift" + ChatColor.WHITE + " : 突进",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一次射出两支箭的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以射中除了正前方以外的两个敌人",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack smokeOni(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.GRAY + "烟中恶鬼");
        String[] lore = {
                ChatColor.GRAY + "SmokeOni",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 释放烟雾,之后变成近战模式",
                ChatColor.YELLOW + "在近战模式中:",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 突进",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 对烟雾中的敌人伤害翻倍",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "烟雾缭绕的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "射箭的时候会让烟雾缠绕在箭上",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "释放烟雾之后会变成胁差,使用近战攻击",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack rainBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.AQUA + "代达罗斯风暴弓");
        String[] lore = {
                ChatColor.GRAY + "RainBow",
                ChatColor.YELLOW + "拉满弓射击" + ChatColor.WHITE + " : 释放箭雨",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "你确定这个不是别的游戏里的?",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack iceBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.AQUA + "霜花弓");
        String[] lore = {
                ChatColor.GRAY + "IceBow",
                ChatColor.WHITE + "不用拉满弓也可以" + ChatColor.YELLOW + "满威力射击",
                ChatColor.YELLOW + "命中敌人" + ChatColor.WHITE + " : 造成寒冷效果",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "寒剑”塞壬“在很久之前抛弃的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "上面残留着一丝冷气",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack tripleCrossbow(){
        ItemStack bow = new ItemStack(Material.CROSSBOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "雅凡琳");
        bowMeta.addEnchant(Enchantment.QUICK_CHARGE,2,false);
        String[] lore = {
                ChatColor.GRAY + "TripleCrossbow",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 三连发",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "非常少见的三连发弩",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "外形与乐器相似",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack enchantedAxe(){
        ItemStack stick = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName(ChatColor.AQUA + "魔化飞斧");
        stickMeta.addEnchant(Enchantment.DURABILITY,1,true);
        stickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),20, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),6, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        stickMeta.addAttributeModifier(attribute1,modifier1);
        stickMeta.addAttributeModifier(attribute2,modifier2);
        stickMeta.addAttributeModifier(attribute3,modifier3);
        stickMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "EnchantedAxe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 投掷斧头",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "附魔过后的斧头",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "扔出去之后会自动追踪敌人",
        };
        stick.setItemMeta(stickMeta);
        t.addLore(stick,lore);
        return stick;
    }
    public ItemStack assassinAxe(){
        ItemStack stick = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "落樱神斧");
        stickMeta.addEnchant(Enchantment.DURABILITY,1,false);
        stickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),30, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        stickMeta.addAttributeModifier(attribute1,modifier1);
        stickMeta.addAttributeModifier(attribute2,modifier2);
        stickMeta.addAttributeModifier(attribute3,modifier3);
        stickMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "AssassinAxe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 投掷斧头",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "只要喊了某个男人的名字",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "他的斧头就会自动出现在那个人的头上",
        };
        stick.setItemMeta(stickMeta);
        t.addLore(stick,lore);
        return stick;
    }

    public ItemStack diceHammer(){
        ItemStack hammer = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta hammerMeta = hammer.getItemMeta();
        hammerMeta.setDisplayName(ChatColor.WHITE + "骰子锤");
        String[] lore = {
                ChatColor.GRAY + "DiceHammer",
                ChatColor.YELLOW + "近战攻击" + ChatColor.WHITE + " : 造成随机伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "锤头部分是一个D6骰子的锤子",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "不过不能当成正常骰子用",
        };
        hammer.setItemMeta(hammerMeta);
        t.addLore(hammer,lore);
        return hammer;
    }
    public ItemStack chopAxe(){
        ItemStack stick = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName(ChatColor.RED + "斩斧");
        stickMeta.addEnchant(Enchantment.DURABILITY,1,false);
        stickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),40, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        stickMeta.addAttributeModifier(attribute1,modifier1);
        stickMeta.addAttributeModifier(attribute2,modifier2);
        stickMeta.addAttributeModifier(attribute3,modifier3);
        stickMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "ChopAxe",
                ChatColor.WHITE + "大幅提升" + ChatColor.YELLOW + "断肢效果的概率",
                ChatColor.WHITE + "击杀一定数量的怪物之后会进入" + ChatColor.YELLOW + "嗜血状态",
                ChatColor.YELLOW + "在嗜血状态时:",
                ChatColor.YELLOW + "挥动武器" + ChatColor.WHITE + " : 释放血刃",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把散发不详气息的斧头",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "看起来十分的凶残",
        };
        stick.setItemMeta(stickMeta);
        t.addLore(stick,lore);
        return stick;
    }

    public ItemStack randomBook(){
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookItemMeta = book.getItemMeta();
        bookItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "随机魔法书");
        Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        bookItemMeta.addAttributeModifier(attribute,modifier);
        String[] lore = {
                ChatColor.GRAY + "RandomBook",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放随机魔法",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "书里面塞了一个骰子的魔法书",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "每次翻书都会掷一次骰子",
        };
        book.setItemMeta(bookItemMeta);
        t.addLore(book,lore);
        return book;
    }
    public ItemStack sharpSword(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.AQUA + "锋利铁剑");
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,5,false);
        String[] lore = {
                ChatColor.GRAY  + "SharpSword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 格挡",
                ChatColor.WHITE + "格挡可以在一段时间内抵消" + ChatColor.YELLOW + "一次近战或弓箭伤害",
                ChatColor.YELLOW + "成功格挡" + ChatColor.WHITE + " : 清除负面效果,范围攻击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "打磨过后的铁剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "可以造成更高的伤害了",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }

    public ItemStack monarchSword(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.WHITE + "帝王剑");
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-2.2, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        swordMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY  + "MonarchSword",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把看起来很普通的武器",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "但这把剑还没有发挥她的实力",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack monarchSwordI(){
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.AQUA + "帝王剑(一阶)");
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),13, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-2.0, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),2, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        swordMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY  + "MonarchSwordI",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 帝王飞弹",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "升级了一次的帝王剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "获得了新的能力",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack monarchSwordII(){
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.YELLOW + "帝王剑(二阶)");
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),17.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-1.8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        swordMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY  + "MonarchSwordII",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 帝王飞弹",
                ChatColor.YELLOW + "Shift + 鼠标右键" + ChatColor.WHITE + " : 生命汲取",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "升级了两次的帝王剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "又获得了新的能力",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack monarchSwordIII(){
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "帝王剑(三阶)");
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),20, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-1.6, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        swordMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY  + "MonarchSwordIII",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 帝王飞弹",
                ChatColor.YELLOW + "Shift + 鼠标右键" + ChatColor.WHITE + " : 生命汲取",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "Q" + ChatColor.WHITE + " : 清除所有冷却时间",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "能量全开的帝王剑",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "帝王君临",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack smokeSword(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GRAY + "胁差");
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),15, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),-1.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        String[] lore = {
                ChatColor.GRAY  + "SmokeSword",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 突进",
                ChatColor.WHITE + "攻击在烟雾里的怪物时," + ChatColor.YELLOW + "伤害翻倍",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一把小太刀",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "非常适合偷袭",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }

    public ItemStack meteor(){
        ItemStack sword = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GOLD + "陨石");
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,5,false);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY  + "Meteor",
                ChatColor.WHITE + "一块陨石",
                ChatColor.WHITE + "可以用来击碎特殊护甲",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack monsterStaff(){
        ItemStack sword = new ItemStack(Material.BLAZE_ROD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.YELLOW + "怪物法杖");
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,100,true);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY  + "MonsterStaff",
                ChatColor.WHITE + "右键来记录一个怪物",
                ChatColor.WHITE + "再点击右键将之前的怪物仇恨设定在另一个怪物身上",
                ChatColor.RED + "测试物品,无法获得",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack deathStaff(){
        ItemStack sword = new ItemStack(Material.BONE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GRAY + "死亡法杖");
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,100,true);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY  + "DeathStaff",
                ChatColor.WHITE + "使用右键清除附近的所有怪物",
                ChatColor.RED + "测试物品,无法获得",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack guitarAxe(){
        ItemStack sword = new ItemStack(Material.IRON_AXE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.YELLOW + "吉他斧头");
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,100,true);
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT,2,true);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        String[] lore = {
                ChatColor.GRAY  + "GuitarAxe",
                ChatColor.WHITE + "用劲爆的吉他Solo点燃你的敌人",
                ChatColor.RED + "测试物品,无法获得",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack shieldAxe(){
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setDisplayName(ChatColor.WHITE + "盾斧");
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),12, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-3, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),6, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        axeMeta.addAttributeModifier(attribute1,modifier1);
        axeMeta.addAttributeModifier(attribute2,modifier2);
        axeMeta.addAttributeModifier(attribute3,modifier3);
        axeMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "ShieldAxe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 格挡",
                ChatColor.WHITE + "格挡可以在一段时间内抵消" + ChatColor.YELLOW + "一次近战或弓箭伤害",
                ChatColor.YELLOW + "成功格挡" + ChatColor.WHITE + " : 更强力的战吼",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC +"当你把盾贴在了斧头上",
                ChatColor.WHITE + "" + ChatColor.ITALIC +"就得到了盾斧",
                ChatColor.WHITE + "" + ChatColor.ITALIC +"攻守兼备",
        };
        axe.setItemMeta(axeMeta);
        t.addLore(axe,lore);
        return axe;
    }
    public ItemStack reflexAxe(){
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setDisplayName(ChatColor.GREEN + "奉还斧");
        Attribute attribute1 = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute4 = Attribute.GENERIC_ARMOR;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),15, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-2.8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4 = new AttributeModifier(UUID.randomUUID(),attribute4.name(),8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        axeMeta.addAttributeModifier(attribute1,modifier1);
        axeMeta.addAttributeModifier(attribute2,modifier2);
        axeMeta.addAttributeModifier(attribute3,modifier3);
        axeMeta.addAttributeModifier(attribute4,modifier4);
        String[] lore = {
                ChatColor.GRAY + "ReflexAxe",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 吸收远程攻击",
                ChatColor.YELLOW + "成功清除远程攻击时:",
                ChatColor.WHITE + "积攒数量",
                ChatColor.WHITE + "下一次近战攻击会根据积攒的数量增加伤害",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC +"获得了反射能力的斧头",
                ChatColor.WHITE + "" + ChatColor.ITALIC +"可以吸收远程攻击",
        };
        axe.setItemMeta(axeMeta);
        t.addLore(axe,lore);
        return axe;
    }
    public ItemStack goldenZweiHander(){
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.SWEEPING_EDGE,10,true);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_MOVEMENT_SPEED;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute4 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),-0.1, AttributeModifier.Operation.ADD_SCALAR);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(),attribute3.name(),50, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier4= new AttributeModifier(UUID.randomUUID(),attribute4.name(),-3.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        swordMeta.addAttributeModifier(attribute,modifier);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addAttributeModifier(attribute3,modifier3);
        swordMeta.addAttributeModifier(attribute4,modifier4);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        swordMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "金胡萝卜神的ZweiHander");
        String[] lore = {
                ChatColor.YELLOW + "GoldenCarrotZweiHander",
                ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放随机技能",
                ChatColor.YELLOW + "挥动武器" + ChatColor.WHITE + " : 释放剑气",
                "",
                ChatColor.YELLOW + "金胡萝卜神收藏的双手剑",
                ChatColor.YELLOW + "剑身非常重,比起砍人更像是砸人",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }

    public ItemStack bossGoldenZweiHander(){
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL,5,false);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_MOVEMENT_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),1, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),+0.25, AttributeModifier.Operation.ADD_SCALAR);
        swordMeta.addAttributeModifier(attribute,modifier);
        swordMeta.addAttributeModifier(attribute2,modifier2);
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        swordMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "金胡萝卜神的ZweiHander");
        String[] lore = {
                ChatColor.YELLOW + "金胡萝卜神收藏的双手剑",
                "",
                ChatColor.YELLOW + "" + ChatColor.ITALIC + "剑身用黄金点缀，非常的精致",
                ChatColor.YELLOW + "" + ChatColor.ITALIC + "剑身非常重，比起砍人更像是砸人",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack kelp(){
        ItemStack kelp = new ItemStack(Material.DRIED_KELP);
        ItemMeta kelpItemMeta = kelp.getItemMeta();
        kelpItemMeta.setDisplayName(ChatColor.GREEN + "美好时光海苔");
        String[] lore = {
                ChatColor.WHITE + "" + ChatColor.ITALIC + "十分的美味乃至九分的美味"
        };
        kelp.setItemMeta(kelpItemMeta);
        t.addLore(kelp,lore);
        return kelp;
    }
    public ItemStack bread(){
        ItemStack bread = new ItemStack(Material.BREAD);
        ItemMeta breadItemMeta = bread.getItemMeta();
        breadItemMeta.setDisplayName(ChatColor.GOLD + "法棍");
        String[] lore = {
                ChatColor.WHITE + "" + ChatColor.ITALIC + "已经变硬了,但也不是不能吃"
        };
        bread.setItemMeta(breadItemMeta);
        t.addLore(bread,lore);
        return bread;
    }
    public ItemStack chicken(){
        ItemStack chicken = new ItemStack(Material.COOKED_CHICKEN);
        ItemMeta chickenItemMeta = chicken.getItemMeta();
        chickenItemMeta.setDisplayName(ChatColor.GOLD + "烧鸡");
        String[] lore = {
                ChatColor.WHITE + "" + ChatColor.ITALIC + "1,2,1,2"
        };
        chicken.setItemMeta(chickenItemMeta);
        t.addLore(chicken,lore);
        return chicken;
    }
    public ItemStack VillarPerosa(){
        ItemStack bow = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.GRAY + "VillarPerosa冲锋枪");
        String[] lore = {
                ChatColor.GRAY + "VillarPerosa",
                ChatColor.WHITE + "换弹的时候" + ChatColor.YELLOW + "无法射击",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "双排弹匣冲锋枪",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "射速非常快",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一般人只能看到一个胡萝卜钓竿",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack sentryHelm(){
        ItemStack sword = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.BINDING_CURSE,1,false);
        Attribute attribute = Attribute.GENERIC_ARMOR;
        Attribute attribute1 = Attribute.GENERIC_MOVEMENT_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),attribute.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD);
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-0.15, AttributeModifier.Operation.ADD_SCALAR,EquipmentSlot.HEAD);
        swordMeta.addAttributeModifier(attribute,modifier);
        swordMeta.addAttributeModifier(attribute1,modifier1);
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        swordMeta.setDisplayName(ChatColor.GRAY + "哨兵面具");
        String[] lore = {
                ChatColor.GRAY + "比头大了一圈的钢铁面具",
                ChatColor.GRAY + "非常的厚重,保护性能很好,但有些影响视野",
                ChatColor.GRAY + "而且还会影响移动速度",
        };
        sword.setItemMeta(swordMeta);
        t.addLore(sword,lore);
        return sword;
    }
    public ItemStack monsterBow(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.GRAY + "怪物弓");
        bowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK,2,true);
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE,5,true);
        String[] lore = {
                ChatColor.WHITE + "" + ChatColor.ITALIC + "地牢深处的怪会拿的弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "击退效果很强",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
    public ItemStack rawCola(){
        ItemStack cola = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) cola.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "日本进口生可乐");
        meta.setColor(Color.LIME);
        String[] lore = {
                ChatColor.GRAY + "RawCola",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "从日本进口的生可乐",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "是绿色的,因为没有添加色素",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "喝了之后滨州会跑出来帮你打怪",
        };
        cola.setItemMeta(meta);
        t.addLore(cola,lore);
        return cola;
    }
    public ItemStack sniperRifle(){
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.AQUA + ".303狙击步枪");
        String[] lore = {
                ChatColor.GRAY + "SniperRifle",
                ChatColor.YELLOW + "射击" + ChatColor.WHITE + " : 视野放大",
                "",
                ChatColor.WHITE + "" + ChatColor.ITALIC + ".303弹药狙击步枪",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "一般人只能看到一张弓",
                ChatColor.WHITE + "" + ChatColor.ITALIC + "射击的时候视野会放大",
        };
        bow.setItemMeta(bowMeta);
        t.addLore(bow,lore);
        return bow;
    }
}
