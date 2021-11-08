package ink.ptms.fenestra

import ink.ptms.fenestra.input.*
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.ItemTagType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.inventoryCenterSlots
import java.util.concurrent.CompletableFuture

/**
 * Fenestra
 * ink.ptms.fenestra.WorkspaceInputs
 *
 * @author sky
 * @since 2021/5/18 12:24 上午
 */
object WorkspaceInputs {

    fun nextGenericAction(player: Player, channel: Channel) {
        InputGeneric(player, channel).next()
    }

    fun nextArrayAction(player: Player, channel: Channel, index: Int, byte: Boolean = false, create: Boolean = false, self: Boolean = false) {
        InputArray(player, channel, index, byte, create, self).next()
    }

    fun nextListAction(player: Player, channel: Channel, create: Boolean = false, self: Boolean = false) {
        InputList(player, channel, create, self).next()
    }

    fun nextCompoundAction(player: Player, channel: Channel) {
        InputCompound(player, channel).next()
    }

    fun nextBaseAction(player: Player) {
        InputBase(player).next()
    }

    fun nextType(player: Player): CompletableFuture<ItemTagType?> {
        var closeLogic = true
        val future = CompletableFuture<ItemTagType?>()
        val types = ItemTagType.values().filter { it != ItemTagType.END }
        player.openMenu<Basic>("Fenestra Types") {
            rows(6)
            onBuild { _, inv ->
                types.forEachIndexed { index, type ->
                    inv.setItem(
                        inventoryCenterSlots[index],
                        ItemBuilder(XMaterial.PAPER)
                            .name("§7$type")
                            .lore("§8${player.asLangText("command-input-select")}")
                            .build()
                    )
                }
            }
            onClick {
                if (it.rawSlot in inventoryCenterSlots && it.currentItem?.type == Material.PAPER) {
                    closeLogic = false
                    player.closeInventory()
                    future.complete(types[inventoryCenterSlots.indexOf(it.rawSlot)])
                }
            }
            onClose {
                if (closeLogic) {
                    future.complete(null)
                }
            }
        }
        return future
    }
}