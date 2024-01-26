package fr.codinbox.shulkop;

import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ShulkOpPlugin extends JavaPlugin implements Listener {

    public static boolean enabled = true;
    private static final HashMap<UUID, OpenedShulkerBox> openedShulkerBoxes = new HashMap<>();

    public static void openShulkerBox(@NotNull ItemStack itemStack, @NotNull Player player) {
        if (!itemStack.getType().name().contains("SHULKER_BOX"))
            return;
        BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        player.openInventory(shulkerBox.getInventory());
        openedShulkerBoxes.put(player.getUniqueId(), new OpenedShulkerBox(meta, shulkerBox, itemStack));
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getServer().getPluginCommand("shulkop")).setExecutor(new ShulkOpCommand());
    }

    @Override
    public void onDisable() {
        for (Map.Entry<UUID, OpenedShulkerBox> entry : openedShulkerBoxes.entrySet()) {
            var player = getServer().getPlayer(entry.getKey());
            if (player == null)
                continue;

            var o = entry.getValue();
            // Update shulker box meta
            var meta = o.meta;
            meta.setBlockState(o.shulkerBox);
            openedShulkerBoxes.remove(player.getUniqueId());

            // Update item in player inventory
            if (o.item != null)
                o.item.setItemMeta(meta);

            // Close inventory
            player.closeInventory();
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!enabled)
            return;
        var player = event.getPlayer();
        var item = event.getItem();
        if (!player.isSneaking() || item == null || !item.getType().name().contains("SHULKER_BOX"))
            return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            openShulkerBox(item, player);
            event.setCancelled(true); // Cancel shulker placing to prevent duplication
        }
   }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        var player = (Player) event.getPlayer();
        var o = openedShulkerBoxes.get(player.getUniqueId());
        if (o == null)
            return;

        // Update shulker box meta
        var meta = o.meta;
        meta.setBlockState(o.shulkerBox);
        openedShulkerBoxes.remove(player.getUniqueId());

        // Update item in player inventory
        if (o.item != null)
            o.item.setItemMeta(meta);
    }

    @EventHandler
    private void onInventoryInteract(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var o = openedShulkerBoxes.get(player.getUniqueId());
        var clickedInventory = event.getClickedInventory();

        if (clickedInventory == null || clickedInventory != player.getInventory() || o == null)
            return;
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(o.item))
            event.setCancelled(true); // Cancel shulker box movement if it's the one opened
        if (event.getHotbarButton() != -1) {
            var item = player.getInventory().getItem(event.getHotbarButton());
            if (item != null && item.equals(o.item))
                event.setCancelled(true); // Cancel shulker box movement if it's the one opened
        }
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        var o = openedShulkerBoxes.get(player.getUniqueId());
        if (o == null)
            return;

        // Update shulker box meta
        var meta = o.meta;
        meta.setBlockState(o.shulkerBox);
        openedShulkerBoxes.remove(player.getUniqueId());

        // Update item in player inventory
        if (o.item != null)
            o.item.setItemMeta(meta);
        player.closeInventory();
    }

}
