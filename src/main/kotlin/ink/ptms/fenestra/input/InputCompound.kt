package ink.ptms.fenestra.input

import ink.ptms.fenestra.Channel
import ink.ptms.fenestra.FenestraAPI.workspace
import ink.ptms.fenestra.ItemBuilder
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText

class InputCompound(player: Player, val channel: Channel) : Input(player) {

    override fun next() {
        closeEvent[1] = true
        player.openMenu<Basic>("Fenestra Compound") {
            rows(3)
            map("", "  1 2 3  ")
            set(
                '1', ItemBuilder(XMaterial.LAVA_BUCKET)
                    .name(player.asLangText("workspace-generic-delete"))
                    .build()
            )
            set(
                '2', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(player.asLangText("workspace-generic-create"))
                    .build()
            )
            set(
                '3', ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .name(player.asLangText("workspace-generic-create-children"))
                    .build()
            )
            onClick { c1 ->
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
            }
            onClose {
                if (closeEvent[1]!!) {
                    player.cancel()
                }
            }
        }
    }
}