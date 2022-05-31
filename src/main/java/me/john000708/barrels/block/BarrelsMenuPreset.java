package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
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
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

import javax.annotation.Nonnull;

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
    public void newInstance(@Nonnull BlockMenu menu, Block b) {

        registerEvent((slot, prev, next) -> {
            barrel.updateBarrel(b);
            return next;
        });

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
            menu.replaceExistingItem(4, new CustomItem(Material.BARRIER, "&7空"), false);
            menu.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7空"), false);
        }
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border1) {
            preset.addItem(i, new CustomItem(Material.CYAN_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border2) {
            preset.addItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border3) {
            preset.addItem(i, new CustomItem(Material.ORANGE_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        preset.addMenuClickHandler(4, ChestMenuUtils.getEmptyClickHandler());
        preset.addMenuClickHandler(22, ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
        return SlimefunPlugin.getProtectionManager().hasPermission(p, b, ProtectableAction.INTERACT_BLOCK);
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
        return new int[0];
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        if (flow == ItemTransportFlow.INSERT) {
            if (BlockStorage.getLocationInfo(((BlockMenu) menu).getLocation(), "storedItems") != null) {
                if (SlimefunUtils.isItemSimilar(item, menu.getItemInSlot(22), true, false)) {
                    return barrel.getInputSlots(); // != null is
                }
                else {
                    return new int[0]; // != null isnt
                }
            }
            else {
                return barrel.getInputSlots(); // == null
            }
        }
        else {
            return barrel.getOutputSlots();
        }
    }

}
