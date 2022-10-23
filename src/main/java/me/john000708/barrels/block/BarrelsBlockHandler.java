package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

class BarrelsBlockHandler implements SlimefunBlockHandler {

    private final Barrel barrel;

    public BarrelsBlockHandler(Barrel barrel) {
        this.barrel = barrel;
    }

    @Override
    public boolean onBreak(Player player, Block b, SlimefunItem slimefunItem, UnregisterReason unregisterReason) {

        if(BlockStorage.getLocationInfo(b.getLocation(), "storedItems") != null &&  Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems")) > 1024){
            player.sendMessage(ChatColors.color("&aSlimefun 4 &7> &e單元內物品須低於1024個才能破壞!"));
            return false;
        }

        BlockMenu inv = BlockStorage.getInventory(b);

        inv.dropItems(b.getLocation(), barrel.getInputSlots());
        inv.dropItems(b.getLocation(), barrel.getOutputSlots());

        String storedAmountString = BlockStorage.getLocationInfo(b.getLocation(), "storedItems");
        if (storedAmountString == null) {
            return true;
        }
        int storedAmount = Integer.parseInt(storedAmountString);

        ItemStack item = inv.getItemInSlot(22);

        while (storedAmount > 0) {
            int amount = item.getMaxStackSize();

            if (storedAmount > amount) {
                storedAmount -= amount;
            }
            else {
                amount = storedAmount;
                storedAmount = 0;
            }

            b.getWorld().dropItem(b.getLocation(), new CustomItemStack(item, amount));
        }
        BlockStorage.addBlockInfo(b, "storedItems", null);
        inv.replaceExistingItem(22, new CustomItemStack(Material.BARRIER, "&7空"), false);

        return true;
    }

}
