package me.john000708.barrels;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.john000708.barrels.block.Barrel;
import me.john000708.barrels.listeners.WorldListener;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin implements SlimefunAddon {

    private static Barrels instance;

    private boolean requirePlastic;
    private String itemFormat;

    @Override
    public void onEnable() {
        instance = this;
        Config config = new Config(this);

        new WorldListener(this);

        requirePlastic = config.getBoolean("options.plastic-recipe");
        itemFormat = config.getString("options.item-format");

        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void setup() {
        Category barrelCat = new Category(new NamespacedKey(this, "barrels"), new CustomItemStack(Material.OAK_LOG, "&3儲存單元", "", "&a> 點擊開啟"), 2);

        SlimefunItemStack smallBarrel = new SlimefunItemStack("BARREL_SMALL", Material.OAK_LOG, "&6小型&9儲存單元", "", "&8\u21E8 &7容量: 64 組物品");
        SlimefunItemStack mediumBarrel = new SlimefunItemStack("BARREL_MEDIUM", Material.SPRUCE_LOG, "&7中型&9儲存單元", "", "&8\u21E8 &7容量: 128 組物品");
        SlimefunItemStack bigBarrel = new SlimefunItemStack("BARREL_BIG", Material.DARK_OAK_LOG, "&3大型&9儲存單元", "", "&8\u21E8 &7容量: 256 組物品");
        SlimefunItemStack largeBarrel = new SlimefunItemStack("BARREL_LARGE", Material.ACACIA_LOG, "&5超大&9儲存單元", "", "&8\u21E8 &7容量: 512 組物品");
        SlimefunItemStack deepStorageUnit = new SlimefunItemStack("BARREL_GIGANTIC", Material.DIAMOND_BLOCK, "&2無盡&9儲存單元", "", "&4終極機器", "", "&8\u21E8 &7容量: 16384 組物品");

        new Barrel(barrelCat, smallBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CHEST), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 4096) {

            @Override
            public String getInventoryTitle() {
                return "&6小型 &7- &9儲存單元";
            }

        }.register(this);

        new Barrel(barrelCat, mediumBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), smallBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 8192) {

            @Override
            public String getInventoryTitle() {
                return "&7中型 &7- &9儲存單元";
            }

        }.register(this);
        Research medium = new Research(new NamespacedKey(this, "barrel_medium"),2001, "儲存的新紀元", 60);
        medium.addItems(mediumBarrel);
        medium.register();

        new Barrel(barrelCat, bigBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), mediumBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 16384) {

            @Override
            public String getInventoryTitle() {
                return "&3大型 &7- &9儲存單元";
            }

        }.register(this);
        Research big = new Research(new NamespacedKey(this, "barrel_big"),2002, "優質收納", 80);
        big.addItems(bigBarrel);
        big.register();

        new Barrel(barrelCat, largeBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), bigBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 32768) {

            @Override
            public String getInventoryTitle() {
                return "&5超大 &7- &9儲存單元";
            }

        }.register(this);
        Research large = new Research(new NamespacedKey(this, "barrel_large"),2003, "加大空間", 100);
        large.addItems(largeBarrel);
        large.register();

        new Barrel(barrelCat, deepStorageUnit, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.PLASTIC_SHEET, largeBarrel, SlimefunItems.PLASTIC_SHEET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE}, 1048576) {

            @Override
            public String getInventoryTitle() {
                return "&2無盡 &7- &9儲存單元";
            }

        }.register(this);
        Research dsu = new Research(new NamespacedKey(this, "barrel_dsu"),2004, "不可思議的空間", 200);
        dsu.addItems(deepStorageUnit);
        dsu.register();
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/John000708/Barrels/issues";
    }

    public static Barrels getInstance() {
        return instance;
    }

    public static String getItemFormat() {
        return instance.itemFormat;
    }

}
