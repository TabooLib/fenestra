package ink.ptms.fenestra.input

import ink.ptms.fenestra.ItemBuilder
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText

class InputBase(player: Player) : Input(player) {

    override fun next() {
        closeEvent[1] = true
        player.openMenu<Basic>("Fenestra") {
            rows(3)
            map("", "    1    ")
            set(
                '1', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(player.asLangText("workspace-generic-create"))
                    .build()
            )
            onClick {
                if (it.slot == '1') {
                    createBaseNode()
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