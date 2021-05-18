package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
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

    fun nextGenericAction(player: Player, nbt: NBTBase) {
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra Generic")
            .rows(3)
            .items("", "  1 2 3  ")
            .put(
                '1', ItemBuilder(XMaterial.LAVA_BUCKET)
                    .name(TLocale.asString(player, "workspace-generic-delete"))
                    .build()
            )
            .put(
                '2', ItemBuilder(XMaterial.BOOK)
                    .name(TLocale.asString(player, "workspace-generic-edit"))
                    .build()
            )
            .put(
                '3', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            ).click {

            }.close {
                player.cancel()
            }.open(player)
    }

    fun nextArrayAction(player: Player, nbt: NBTBase, index: Int, byte: Boolean = false, create: Boolean = false, self: Boolean = false) {
        MenuBuilder.builder(Fenestra.plugin)
            .title(if (byte) "Fenestra Byte Array" else "Fenestra Int Array")
            .rows(3)
            .also {
                when {
                    create -> {
                        it.items("", "  0 3 0  ")
                    }
                    self -> {
                        it.items("", "  1 3 4 ")
                    }
                    else -> {
                        it.items("", "  1 2 3  ")
                    }
                }
            }
            .put(
                '1', ItemBuilder(XMaterial.LAVA_BUCKET)
                    .name(TLocale.asString(player, "workspace-generic-delete"))
                    .build()
            )
            .put(
                '2', ItemBuilder(XMaterial.BOOK)
                    .name(TLocale.asString(player, "workspace-generic-edit"))
                    .build()
            )
            .put(
                '3', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .put(
                '4', ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create-children"))
                    .build()
            ).click {

            }.close {
                player.cancel()
            }.open(player)
    }

    fun nextListAction(player: Player, nbt: NBTBase) {
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra List")
            .rows(3)
            .items("", "  1 2 3  ")
            .put(
                '1', ItemBuilder(XMaterial.LAVA_BUCKET)
                    .name(TLocale.asString(player, "workspace-generic-delete"))
                    .build()
            )
            .put(
                '2', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .put(
                '3', ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create-children"))
                    .build()
            ).click {

            }.close {
                player.cancel()
            }.open(player)
    }

    fun nextCompoundAction(player: Player, nbt: NBTBase) {
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra Compound")
            .rows(3)
            .items("", "  1 2 3  ")
            .put(
                '1', ItemBuilder(XMaterial.LAVA_BUCKET)
                    .name(TLocale.asString(player, "workspace-generic-delete"))
                    .build()
            )
            .put(
                '2', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .put(
                '3', ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create-children"))
                    .build()
            ).click {

            }.close {
                player.cancel()
            }.open(player)
    }

    fun nextBaseAction(player: Player) {
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra")
            .rows(3)
            .items("", "    1  ")
            .put(
                '1', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .click {

            }.close {
                player.cancel()
            }.open(player)
    }

    private fun Player.cancel() {
        workspace?.run {
            cancelState()
            send()
        }
    }
}