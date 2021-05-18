package ink.ptms.fenestra

import ink.ptms.fenestra.input.*
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
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

    fun nextType(player: Player): CompletableFuture<NBTType?> {
        var closeLogic = true
        val future = CompletableFuture<NBTType?>()
        val types = NBTType.values().filter { it != NBTType.END }
        MenuBuilder.builder()
            .title("Fenestra Types")
            .rows(6)
            .build {
                types.forEachIndexed { index, type ->
                    it.setItem(
                        Items.INVENTORY_CENTER[index], ItemBuilder(Material.PAPER)
                            .name("§7$type")
                            .lore("§8${TLocale.asString(player, "command-input-select")}")
                            .build()
                    )
                }
            }.click {
                if (it.rawSlot in Items.INVENTORY_CENTER && it.currentItem?.type == Material.PAPER) {
                    closeLogic = false
                    player.closeInventory()
                    future.complete(types[Items.INVENTORY_CENTER.indexOf(it.rawSlot)])
                }
            }.close {
                if (closeLogic) {
                    future.complete(null)
                }
            }.open(player)
        return future
    }
}