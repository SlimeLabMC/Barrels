package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

class BarrelsMenuPreset extends BlockMenuPreset {

    private static final int[] border1 = { 0, 1, 2, 9, 11, 18, 19, 20 };
    private static final int[] border2 = { 3, 5, 12, 13, 14, 21, 23 };
    private static final int[] border3 = { 6, 7, 8, 15, 17, 24, 25, 26 };

    private final Barrel barrel;

    public BarrelsMenuPreset(Barrel barrel) {
        super(barrel.getId(), barrel.getInventoryTitle());

        this.barrel = barrel;
    }

    @Override
    public void init() {
        constructMenu(this);
    }

    @Override
    public void newInstance(BlockMenu menu, Block b) {
        barrel.updateCapacityItem(menu, barrel.getCapacity(b), 0);
        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
            menu.replaceExistingItem(22, new CustomItemStack(Material.BARRIER, "&7空"), false);
        }
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border1) {
            preset.addItem(i, new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border2) {
            preset.addItem(i, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border3) {
            preset.addItem(i, new CustomItemStack(Material.ORANGE_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        preset.addMenuClickHandler(4, ChestMenuUtils.getEmptyClickHandler());
        preset.addMenuClickHandler(22, ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    public boolean canOpen(Block b,  Player p) {
        return SlimefunPlugin.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK);
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
        return new int[0];
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        if (flow == ItemTransportFlow.INSERT) {
            if (BlockStorage.getLocationInfo(((BlockMenu)menu).getLocation(), "storedItems") == null ||
                    barrel.getStoredItem(menu) == null ||
                    SlimefunUtils.isItemSimilar(item, barrel.getStoredItem(menu), true, false)) {
                return barrel.getInputSlots();
            }
            else {
                return new int[0];
            }
        }
        else {
            return barrel.getOutputSlots();
        }
    }

}
