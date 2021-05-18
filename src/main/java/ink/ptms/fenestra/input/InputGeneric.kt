package ink.ptms.fenestra.input

import ink.ptms.fenestra.Channel
import ink.ptms.fenestra.Fenestra
import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.entity.Player

/**
 * Fenestra
 * ink.ptms.fenestra.input.InputGeneric
 *
 * @author sky
 * @since 2021/5/18 5:31 下午
 */
class InputGeneric(player: Player, val channel: Channel) : Input(player) {

    override fun next() {
        closeEvent[1] = true
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
            ).click { c1 ->
                when (c1.slot) {
                    '1' -> {
                        player.workspace?.removeChannel(channel)
                        player.closeInventory()
                    }
                    '2' -> {
                        updateNodeChoose(channel)
                    }
                    '3' -> {
                        createNode(channel)
                    }
                }
            }.close {
                if (closeEvent[1]!!) {
                    player.cancel()
                }
            }.open(player)
    }
}