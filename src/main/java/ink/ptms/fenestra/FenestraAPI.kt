package ink.ptms.fenestra

import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.util.item.Items
import org.bukkit.entity.Player

object FenestraAPI {

    /**
     * [Fenestra] Edit Panel:
     * [Fenestra] display:
     * [Fenestra]   Name: '1'
     * [Fenestra]   Lore:
     * [Fenestra]   - '1'
     * [Fenestra]   - '2'
     */
    fun edit(player: Player) {
        val itemInMainHand = player.inventory.itemInMainHand
        if (Items.isNull(itemInMainHand)) {
            player.sendLocale("edit-item-air")
            return
        }
    }
}