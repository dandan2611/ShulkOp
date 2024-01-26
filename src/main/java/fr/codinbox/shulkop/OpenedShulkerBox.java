package fr.codinbox.shulkop;

import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class OpenedShulkerBox {

    protected BlockStateMeta meta;
    protected ShulkerBox shulkerBox;
    protected ItemStack item;

    public OpenedShulkerBox(BlockStateMeta meta, ShulkerBox shulkerBox, ItemStack item) {
        this.meta = meta;
        this.shulkerBox = shulkerBox;
        this.item = item;
    }

}
