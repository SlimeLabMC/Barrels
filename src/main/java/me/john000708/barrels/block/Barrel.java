package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.john000708.barrels.Barrels;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by John on 06.05.2016.
 */
public abstract class Barrel extends SimpleSlimefunItem<BlockTicker> {

    private final int capacity;
    private final String erroritem = ChatColors.color("&c出現錯誤 請勿破壞本單元並聯絡管理員");

    protected Barrel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int capacity) {
        super(category, item, recipeType, recipe);

        this.capacity = capacity;

        new BarrelsMenuPreset(this);
        registerBlockHandler(getId(), new BarrelsBlockHandler(this));
    }

    public abstract String getInventoryTitle();

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {

            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                try{
                    updateBarrel(block);
                }catch (StackOverflowError e){
                    Slimefun.getLogger().log(Level.SEVERE, "Barrel StackOverflowError " + block.getLocation());
                    Slimefun.getLogger().log(Level.SEVERE, shortenedStackTrace(e, 20));
                }
            }
        };
    }

    public static String shortenedStackTrace(Error e, int maxLines) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String[] lines = writer.toString().split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
            sb.append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    public int getCapacity(Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "capacity") == null) {
            BlockStorage.addBlockInfo(b, "capacity", String.valueOf(this.capacity));
        }

        // There's no need to box the integer.
        return Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
    }

    public int[] getInputSlots() {
        return new int[] { 10 };
    }

    public int[] getOutputSlots() {
        return new int[] { 16 };
    }

    public ItemStack getStoredItem(DirtyChestMenu inventory) {
        ItemStack item = inventory.getItemInSlot(22);
        return item.getType() == Material.BARRIER ? null : item;
    }

    protected void updateCapacityItem(BlockMenu inventory, int capacity, int storedItem) {
        StringBuilder bar = new StringBuilder(64);
        float rate = (float) storedItem / (float) capacity;

        bar.append("&8[");

        switch ((int) (rate * 4)) {
            case 0 -> bar.append("&2");
            case 1 -> bar.append("&a");
            case 2 -> bar.append("&e");
            default -> bar.append("&c");
        }

        String gauge = "::::::::::::::::::::";
        bar.append(gauge);
        bar.insert(bar.length() - gauge.length() + Math.max(0, Math.min(gauge.length(), (int) (gauge.length() * rate))), "&7");

        bar.append("&8] &7- ").append((int) (rate * 100.0F)).append("%");

        inventory.replaceExistingItem(4, new CustomItemStack(Material.CAULDRON, "&7" + storedItem + "/" + capacity, bar.toString()), false);
    }

    void updateBarrel(Block b) {
        BlockMenu inventory = BlockStorage.getInventory(b);

        if (inventory == null) {
            return;
        }

        ItemStack storedItem = inventory.getItemInSlot(22);

        if(storedItem == null) {
            //BlockStorage.addBlockInfo(b, "storedItems", null);
            Barrels.getInstance().getLogger().log(Level.SEVERE, "null capacity item detected Loc: " + b.getLocation().getBlockX() + " " + b.getLocation().getBlockY() + " " + b.getLocation().getBlockZ());
            inventory.replaceExistingItem(4, new CustomItemStack(Material.BARRIER, erroritem), false);
            inventory.replaceExistingItem(22, new CustomItemStack(Material.BARRIER, erroritem), false);
            return;
        }

        if(storedItem.hasItemMeta() && storedItem.getItemMeta().hasLore()){

            // Error detect
            if(storedItem.getItemMeta().hasDisplayName() && storedItem.getItemMeta().getDisplayName().equals(erroritem)){
                return;
            }

            ItemMeta meta = storedItem.getItemMeta();
            List<String> lore = meta.getLore();
            if(ChatColor.stripColor(lore.get(lore.size() - 1)).equals("")){
                lore.remove(lore.size() - 1);
                meta.setLore(lore);
                storedItem.setItemMeta(meta);
            }
        }

        String storedAmountInfo = BlockStorage.getLocationInfo(b.getLocation(), "storedItems");
        int storedAmount = storedAmountInfo == null ? 0 : Integer.parseInt(storedAmountInfo);
        int capacity = getCapacity(b);

        if(storedItem.getType() == Material.BARRIER && storedAmount != 0) {
            BlockStorage.addBlockInfo(b, "storedItems", null);
            inventory.replaceExistingItem(22, new CustomItemStack(Material.BARRIER, "&7空"), false);
            inventory.replaceExistingItem(16, new CustomItemStack(Material.AIR), false);
            updateCapacityItem(inventory, capacity, 0);
            return;
        }

        for (int slot : getInputSlots()) {
            if (inventory.getItemInSlot(slot) != null) {
                ItemStack input = inventory.getItemInSlot(slot);

                if (input.getType() == Material.BARRIER) {
                    continue;
                }

                int amount = input.getAmount();

                if (SlimefunUtils.isItemSimilar(input, getStoredItem(inventory), true, false)) {
                    if (storedAmount < capacity) {
                        if (storedAmount + amount > capacity) {
                            input.setAmount(storedAmount + input.getAmount() - capacity);
                            inventory.replaceExistingItem(slot, input, false);
                            storedAmount = capacity;
                        }
                        else {
                            inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                            storedAmount += input.getAmount();
                        }
                        BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
                        updateCapacityItem(inventory, capacity, storedAmount);
                    }
                }
                else if (getStoredItem(inventory) == null) {
                    storedAmount = input.getAmount();
                    BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
                    input.setAmount(input.getMaxStackSize());
                    inventory.replaceExistingItem(22, input, false);
                    inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                    updateCapacityItem(inventory, capacity, storedAmount);
                }
            }
        }

        if (storedAmount == 0) {
            return;
        }

        ItemStack stored = getStoredItem(inventory);
        if(stored == null) return;

        for (int slot : getOutputSlots()) {
            ItemStack requestedItem = inventory.getItemInSlot(slot);
            ItemStack output = stored.clone();

            int pushAmount = output.getMaxStackSize();

            if (requestedItem == null || requestedItem.getType() == Material.AIR) {
                pushAmount = Math.min(storedAmount, pushAmount);
                output.setAmount(pushAmount);
            }
            else {
                if (requestedItem.getAmount() == requestedItem.getMaxStackSize() ||
                        !SlimefunUtils.isItemSimilar(requestedItem, output, true, false)) {
                    continue;
                }

                pushAmount = Math.min(storedAmount, requestedItem.getMaxStackSize() - requestedItem.getAmount());
                output.setAmount(requestedItem.getAmount() + pushAmount);
            }

            inventory.replaceExistingItem(slot, output);
            storedAmount -= pushAmount;


            if (storedAmount <= 0) {
                BlockStorage.addBlockInfo(b, "storedItems", null);
                inventory.replaceExistingItem(22, new CustomItemStack(Material.BARRIER, "&7空"), false);
            }
            else {
                BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
            }
            updateCapacityItem(inventory, capacity, storedAmount);
        }
        //Barrels.getInstance().getLogger().log(Level.WARNING, "3");
    }
}
