package ink.ptms.fenestra.input

import ink.ptms.fenestra.Channel
import ink.ptms.fenestra.Fenestra
import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.entity.Player

class InputList(
    player: Player,
    val channel: Channel,
    val create: Boolean = false,
    val self: Boolean = false
) : Input(player) {

    override fun next() {
        closeEvent[1] = true
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra List")
            .rows(3)
            .also {
                when {
                    create -> {
                        it.items("", "   1 3   ")
                    }
                    self -> {
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
                '2', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .put(
                '3', ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create-children"))
                    .build()
            ).click { c1 ->
                when (c1.slot) {
                    '1' -> {
                        player.workspace?.removeChannel(channel)
                        player.closeInventory()
                    }
                    '2' -> {
                        createNode(channel)
                    }
                    '3' -> {
                        createNode(channel, children = true)
                    }
                }
            }.close {
                if (closeEvent[1]!!) {
                    player.cancel()
                }
            }.open(player)
    }
}