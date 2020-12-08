package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by John on 06.05.2016.
 */
public abstract class Barrel extends SimpleSlimefunItem<BlockTicker> {

    private final int capacity;

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
                updateBarrel(block);

                if (Barrels.displayItem() && !block.isEmpty()) {
                    boolean hasRoom = block.getRelative(BlockFace.UP).isEmpty();
                    DisplayItem.updateDisplayItem(block, getCapacity(block), hasRoom);
                }
            }
        };
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

    private ItemStack getCapacityItem(Block b) {
        StringBuilder bar = new StringBuilder();

        // There's no need to box the integer.
        int storedItems = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
        float percentage = Math.round((float) storedItems / (float) getCapacity(b) * 100.0F);

        bar.append("&8[");

        if (percentage < 25) {
            bar.append("&2");
        }
        else if (percentage < 50) {
            bar.append("&a");
        }
        else if (percentage < 75) {
            bar.append("&e");
        }
        else {
            bar.append("&c");
        }

        int lines = 20;

        for (int i = (int) percentage; i >= 5; i -= 5) {
            bar.append(":");
            lines--;
        }

        bar.append("&7");

        for (int i = 0; i < lines; i++) {
            bar.append(":");
        }

        bar.append("&8] &7- ").append(percentage).append("%");

        return new CustomItem(Material.CAULDRON, "&7" + BlockStorage.getLocationInfo(b.getLocation(), "storedItems") + "/" + getCapacity(b), ChatColors.color(bar.toString()));
    }

    void updateBarrel(Block b) {
        BlockMenu inventory = BlockStorage.getInventory(b);

        if (inventory == null) {
            return;
        }

        if(inventory.getItemInSlot(22) == null) {
            Barrels.getInstance().getLogger().log(Level.WARNING, "22 Null "+ b.getLocation().toString());
            BlockStorage.retrieve(b);
            BlockStorage.clearBlockInfo(b);
            return;
        }

        if(inventory.getItemInSlot(22).hasItemMeta() && inventory.getItemInSlot(22).getItemMeta().hasLore()){
            ItemMeta meta = inventory.getItemInSlot(22).getItemMeta();
            List<String> lore = meta.getLore();
            if(ChatColor.stripColor(lore.get(lore.size() - 1)).equals("")){
                lore.remove(lore.size() - 1);
                meta.setLore(lore);
                inventory.getItemInSlot(22).setItemMeta(meta);
            }
        }

        for (int slot : getInputSlots()) {
            if (inventory.getItemInSlot(slot) != null) {
                ItemStack input = inventory.getItemInSlot(slot);
                int amount = input.getAmount();

                if (SlimefunUtils.isItemSimilar(input, inventory.getItemInSlot(22), true, false)) {
                    if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
                        BlockStorage.addBlockInfo(b, "storedItems", "0");
                    }

                    int storedAmount = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));

                    if (storedAmount < getCapacity(b)) {
                        if (storedAmount + amount > getCapacity(b)) {
                            BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(getCapacity(b)));
                            inventory.replaceExistingItem(slot, InvUtils.decreaseItem(inventory.getItemInSlot(slot), getCapacity(b) - storedAmount), false);
                        }
                        else {
                            BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount + amount));
                            inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                        }
                        inventory.replaceExistingItem(4, getCapacityItem(b), false);
                    }
                }
                else if (inventory.getItemInSlot(22).getType() == Material.BARRIER) {
                    BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(amount));

                    input.setAmount(input.getMaxStackSize());
                    inventory.replaceExistingItem(22, input, false);
                    inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                    inventory.replaceExistingItem(4, getCapacityItem(b), false);
                }
            }
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
            //Barrels.getInstance().getLogger().log(Level.WARNING, "6");
            return;
        }

        int stored = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
        if(stored == 0) {
            clear(b, inventory);
            //Barrels.getInstance().getLogger().log(Level.WARNING, "5");
            return;
        }
        ItemStack output = inventory.getItemInSlot(22).clone();

        if (inventory.getItemInSlot(getOutputSlots()[0]) != null && inventory.getItemInSlot(getOutputSlots()[0]).getType() != Material.AIR) {
            if (!SlimefunUtils.isItemSimilar(inventory.getItemInSlot(getOutputSlots()[0]), output, true, false)) {
                //Barrels.getInstance().getLogger().log(Level.WARNING, "4");
                return;
            }

            int requested = output.getMaxStackSize() - inventory.getItemInSlot(getOutputSlots()[0]).getAmount();
            output.setAmount(Math.min(stored, requested));
        }
        else {
            output.setAmount(Math.min(stored, output.getMaxStackSize()));
        }

        if (!inventory.fits(output, getOutputSlots())) {
            //Barrels.getInstance().getLogger().log(Level.WARNING, "1");
            return;
        }

        BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(stored - output.getAmount()));
        inventory.pushItem(output, getOutputSlots());

        if (inventory.getItemInSlot(22).getType() == Material.AIR || (stored - output.getAmount()) <= 0) {
            clear(b, inventory);
            //Barrels.getInstance().getLogger().log(Level.WARNING, "2");
            return;
        }

        inventory.replaceExistingItem(4, getCapacityItem(b), false);
        //Barrels.getInstance().getLogger().log(Level.WARNING, "3");
    }

    public void clear(Block b, BlockMenu inventory){
        BlockStorage.addBlockInfo(b, "storedItems", null);
        inventory.replaceExistingItem(4, new CustomItem(Material.BARRIER, "&7空"), false);
        inventory.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7空"), false);
    }
}
