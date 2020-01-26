package me.john000708.barrels;

import me.mrCookieSlime.Slimefun.Objects.Research;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.john000708.barrels.listeners.DisplayListener;
import me.john000708.barrels.listeners.WorldListener;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin {

    public static boolean displayItem;
    public static JavaPlugin plugin;
    public static Config config;

    //Can be private.
    private boolean plastic;

    public void onEnable() {
        plugin = this;

        PluginUtils utils = new PluginUtils(this);
        utils.setupConfig();
        config = utils.getConfig();

        new DisplayListener();
        new WorldListener();

        displayItem = config.getBoolean("options.displayItem");
        plastic = config.getBoolean("options.plastic-recipe");

        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        plugin = null;
    }

    private void setup() {
        Category barrelCat = new Category(new CustomItem(new ItemStack(Material.OAK_LOG), "&3儲存單元", "", "&a> 點擊開啟"), 2);

        ItemStack SMALL_BARREL = new CustomItem(new ItemStack(Material.OAK_LOG), "&6小型&9儲存單元", "", "&8\u21E8 &7容量: 64 組物品");
        ItemStack MEDIUM_BARREL = new CustomItem(Material.SPRUCE_LOG, "&7中型&9儲存單元", "", "&8\u21E8 &7容量: 128 組物品");
        ItemStack BIG_BARREL = new CustomItem(Material.DARK_OAK_LOG, "&3大型&9儲存單元", "", "&8\u21E8 &7容量: 256 組物品");
        ItemStack LARGE_BARREL = new CustomItem(new ItemStack(Material.ACACIA_LOG), "&5超大&9儲存單元", "", "&8\u21E8 &7容量: 512 組物品");
        ItemStack DSU = new CustomItem(new ItemStack(Material.DIAMOND_BLOCK), "&2無盡&9儲存單元", "", "&4終極機器", "", "&8\u21E8 &7容量: 16384 組物品");

        new Barrel(barrelCat, SMALL_BARREL, "BARREL_SMALL", "&6小型&9儲存單元", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CHEST), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 4096).register();

        Barrel Medium = new Barrel(barrelCat, MEDIUM_BARREL, "BARREL_MEDIUM", "&7中型&9儲存單元",  RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.GILDED_IRON, plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SMALL_BARREL, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON}, 8192);
        Medium.register();

        Research medium = new Research(2001, "儲存的新紀元", 60);
        medium.addItems(Medium);
        medium.register();

        Barrel Big = new Barrel(barrelCat, BIG_BARREL, "BARREL_BIG", "&3大型&9儲存單元", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), SlimefunItems.REDSTONE_ALLOY, MEDIUM_BARREL, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.REDSTONE_ALLOY,}, 16384);
        Big.register();

        Research big = new Research(2002, "優質收納", 80);
        big.addItems(Big);
        big.register();

        Barrel Large = new Barrel(barrelCat, LARGE_BARREL, "BARREL_LARGE", "&5超大&9儲存單元",  RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_ALLOY_INGOT, plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, BIG_BARREL, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT}, 32768);
        Large.register();

        Research large = new Research(2003, "加大空間", 100);
        large.addItems(Large);
        large.register();

        Barrel Dsu = new Barrel(barrelCat, DSU, "BARREL_GIGANTIC", "&2無盡&9儲存單元", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.POWER_CRYSTAL, LARGE_BARREL, SlimefunItems.CARGO_MANAGER, SlimefunItems.REINFORCED_PLATE, SlimefunItems.CARBONADO, SlimefunItems.REINFORCED_PLATE}, 1048576);
        Dsu.register();

        Research dsu = new Research(2004, "不可思議的空間", 200);
        dsu.addItems(Dsu);
        dsu.register();
    }
}
