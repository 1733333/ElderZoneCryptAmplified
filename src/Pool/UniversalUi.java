package Pool;

import UniversalMethod.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;

public class UniversalUi {
    private static UniversalUi instance = new UniversalUi();
    private UniversalUi(){}
    public static UniversalUi getInstance() {
        return instance;
    }
    Tools t = Tools.getInstance();

    public ItemStack cancel() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "返回");
        String[] lore = {
                ChatColor.RED  + "哎呀，不小心走进传送门了",
                ChatColor.RED  + "我要回去",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack easy() {
        ItemStack barrier = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.WHITE + "我还年轻不想死");
        String[] lore = {
                ChatColor.WHITE  + "简单模式",
                ChatColor.WHITE  + "怪物的数量变少",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack normal() {
        ItemStack barrier = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.YELLOW + "用力打我");
        String[] lore = {
                ChatColor.YELLOW  + "普通模式",
                ChatColor.YELLOW  + "正常的游戏模式",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack hard() {
        ItemStack barrier = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "非常暴力");
        String[] lore = {
                ChatColor.LIGHT_PURPLE  + "困难模式",
                ChatColor.LIGHT_PURPLE  + "怪物的数量增加",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack veryHard() {
        ItemStack barrier = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "噩梦");
        String[] lore = {
                ChatColor.RED  + "噩梦模式",
                ChatColor.RED  + "怪物数量增加",
                ChatColor.RED  + "并且普通怪物全部变成精英怪",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack suicide() {
        ItemStack barrier = new ItemStack(Material.CHORUS_FRUIT);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "红茶果");
        String[] lore = {
                ChatColor.GRAY + "Poison",
                ChatColor.WHITE  + "昏睡果实",
                ChatColor.WHITE  + "吃下去即可回到主城",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack pageUP() {
        ItemStack barrier = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "上一页");
        String[] lore = {
                ChatColor.WHITE + "上一页",
                ChatColor.WHITE + "顾名思义，点击即可回到上一页",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack pageDown() {
        ItemStack barrier = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.GREEN + "下一页");
        String[] lore = {
                ChatColor.WHITE  + "下一页",
                ChatColor.WHITE + "顾名思义，点击即可去到下一页",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack no() {
        ItemStack barrier = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "哎呀,选错难度了");
        String[] lore = {
                ChatColor.WHITE + "感觉刚才选的难度有点难",
                ChatColor.WHITE + "自裁,并回到地牢世界",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack ok() {
        ItemStack barrier = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.GREEN + "我知道该怎么用这把武器了！");
        String[] lore = {
                ChatColor.WHITE  + "开始地牢,生成怪物",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack liveMask() {
        ItemStack barrier = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.addEnchant(Enchantment.DURABILITY,1,false);
        barrierItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        barrierItemMeta.setDisplayName(ChatColor.YELLOW +""+ ChatColor.BOLD + "金面具 ——生之面具");
        String[] lore = {
                ChatColor.WHITE  + "戴上金之面具,踏上轮回之道",
                ChatColor.WHITE + "迎接更强的下一波怪物",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack deathMask() {
        ItemStack barrier = new ItemStack(Material.IRON_HELMET);
        ItemMeta barrierItemMeta = barrier.getItemMeta();;
        barrierItemMeta.addEnchant(Enchantment.DURABILITY,1,false);
        barrierItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        barrierItemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "银面具 ——死之面具");
        String[] lore = {
                ChatColor.WHITE  + "戴上死之面具,以血祭祀地牢",
                ChatColor.WHITE + "回到主城，并获得奖励",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack close() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "关闭菜单");
        String[] lore = {
                ChatColor.RED  + "我是GigaChad,我不需要升级",
                ChatColor.RED  + "直接关闭菜单",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack closeElite() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "关闭菜单");
        String[] lore = {
                ChatColor.RED  + "我还是想要升级属性",
                ChatColor.RED  + "开启属性菜单",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack attackSpeed() {
        ItemStack barrier = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.YELLOW+ "增加攻速");
        String[] lore = {
                ChatColor.YELLOW  + "点击即可增加近战攻速",
                ChatColor.YELLOW  + "攻速增加0.2",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack attackDamage() {
        ItemStack barrier = new ItemStack(Material.IRON_SWORD);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "增加攻击力");
        String[] lore = {
                ChatColor.WHITE + "点击即可增加近战和远程攻击力",
                ChatColor.WHITE + "攻击力增加2",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack armor() {
        ItemStack barrier = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.GRAY + "增加护甲/最大生命值");
        String[] lore = {
                ChatColor.GRAY  + "点击即可增加护甲",
                ChatColor.GRAY  + "护甲达到最大值时会增加最大生命值",
                ChatColor.GRAY  + "护甲/最大生命值增加2",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack movementSpeed() {
        ItemStack barrier = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.AQUA + "增加远程武器移动速度");
        String[] lore = {
                ChatColor.AQUA  + "增加拿着远程武器时的移动速度",
                ChatColor.AQUA  + "移动速度增加10%",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack eliteKit() {
        ItemStack barrier = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "获得精英装备");
        String[] lore = {
                ChatColor.RED  + "选择一套精英装备",
                ChatColor.RED  + "更强力,但也无法进化,将会替代现有装备",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }

    public ItemStack sentryKit(){
        ItemStack barrier = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.GRAY + "哨兵装备");
        String[] lore = {
                ChatColor.GRAY  + "获得哨兵装备",
                ChatColor.GRAY  + "拥有一把VillarPerosa冲锋枪,25发弹容",
                ChatColor.GRAY  + "一次射击5发,子弹打空需要装弹",
                ChatColor.GRAY  + "拥有重甲,但视野受限,而且移动速度降低",
                ChatColor.GRAY  + "装甲对于远程攻击的防护效果不是很好",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack goldenCarrotKit(){
        ItemStack barrier = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.YELLOW + "金胡萝卜神的化身装备");
        String[] lore = {
                ChatColor.YELLOW  + "获得金胡萝卜神的化身的装备",
                ChatColor.YELLOW  + "一把双手巨剑",
                ChatColor.YELLOW  + "双手巨剑可以按鼠标右键释放特殊能力",
                ChatColor.YELLOW  + "但是因为非常重,所以拿着的时候移速会降低",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public ItemStack gigaChadKit(){
        ItemStack barrier = new ItemStack(Material.BRICK);
        ItemMeta barrierItemMeta = barrier.getItemMeta();
        barrierItemMeta.setDisplayName(ChatColor.RED + "GigaChad装备");
        String[] lore = {
                ChatColor.RED + "变成GigaChad模式",
                ChatColor.RED + "攻击力 + 20,但不能使用武器",
                ChatColor.RED + "攻击敌人有概率会缴械敌人",
                ChatColor.RED + "拿着武器攻击敌人,武器会碎掉,但伤害翻倍",
        };
        barrier.setItemMeta(barrierItemMeta);
        t.addLore(barrier, lore);
        return barrier;
    }
    public String difficultyName(int diff){
        String diffString;
        switch (diff) {
            case 0:
                diffString = ChatColor.WHITE + "我还年轻不想死";
                break;
            case 2:
                diffString = ChatColor.RED + "噩梦";
                break;
            case 3:
                diffString = ChatColor.LIGHT_PURPLE + "非常暴力";
                break;
            default:
                diffString = ChatColor.YELLOW + "用力打我";
                break;
        }
        return diffString;
    }
}
