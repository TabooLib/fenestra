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
        InputGeneric.next(player, channel)
    }

    fun nextArrayAction(player: Player, channel: Channel, index: Int, byte: Boolean = false, create: Boolean = false, self: Boolean = false) {
        InputArray.next(player, channel, index, byte, create, self)
    }

    fun nextListAction(player: Player, channel: Channel, create: Boolean = false, self: Boolean = false) {
        InputList.next(player, channel, create, self)
    }

    fun nextCompoundAction(player: Player, channel: Channel) {
        InputCompound.next(player, channel)
    }

    fun nextBaseAction(player: Player) {
        InputBase.next(player)
    }

    fun nextType(player: Player): CompletableFuture<NBTType> {
        val future = CompletableFuture<NBTType>()
        MenuBuilder.builder()
            .build {
                NBTType.values().forEachIndexed { index, type ->
                    it.setItem(
                        Items.INVENTORY_CENTER[index], ItemBuilder(Material.PAPER)
                            .name("§7$type")
                            .lore("§8${TLocale.asString(player, "command-input-select")}")
                            .build()
                    )
                }
            }.click {
                if (it.rawSlot in Items.INVENTORY_CENTER && it.currentItem?.type == Material.PAPER) {
                    player.closeInventory()
                    future.complete(NBTType.values()[Items.INVENTORY_CENTER.indexOf(it.rawSlot)])
                }
            }.open(player)
        return future
    }
}