package me.john000708.barrels.block;

import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

class BarrelsBlockHandler implements SlimefunBlockHandler {

    private final Barrel barrel;

    public BarrelsBlockHandler(Barrel barrel) {
        this.barrel = barrel;
    }

    @Override
    public boolean onBreak(Player player, Block b, SlimefunItem slimefunItem, UnregisterReason unregisterReason) {

        if(BlockStorage.getLocationInfo(b.getLocation(), "storedItems") != null &&  Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems")) > 1024){
            player.sendMessage(ChatColors.color("&aSlimefun 4 &7> &e單元內物品須低於1024個 才能破壞!"));
            return false;
        }

        DisplayItem.removeDisplayItem(b);

        BlockMenu inv = BlockStorage.getInventory(b);

        if (inv.getItemInSlot(barrel.getInputSlots()[0]) != null) {
            b.getWorld().dropItem(b.getLocation(), inv.getItemInSlot(barrel.getInputSlots()[0]));
        }

        if (inv.getItemInSlot(barrel.getOutputSlots()[0]) != null) {
            b.getWorld().dropItem(b.getLocation(), inv.getItemInSlot(barrel.getOutputSlots()[0]));
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
            return true;
        }

        ItemStack item = inv.getItemInSlot(22);
        int storedAmount = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));

        while (storedAmount > 0) {
            int amount = item.getMaxStackSize();

            if (storedAmount > amount) {
                storedAmount -= amount;
            }
            else {
                amount = storedAmount;
                storedAmount = 0;
            }

            b.getWorld().dropItem(b.getLocation(), new CustomItem(item, amount));
        }

        return true;
    }

}
