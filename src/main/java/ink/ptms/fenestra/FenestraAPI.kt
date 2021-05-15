package ink.ptms.fenestra

import io.izzel.taboolib.kotlin.getLocalData
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.util.item.Items
import org.bukkit.entity.Player

object FenestraAPI {

    /**
     * 玩家是否在编辑模式中
     */
    var Player.isEditMode: Boolean
        set(it) = getLocalData().set("fenestra.edit-mode", it)
        get() = getLocalData().getBoolean("fenestra.edit-mode")

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