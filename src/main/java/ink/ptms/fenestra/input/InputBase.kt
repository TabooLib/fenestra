package ink.ptms.fenestra.input

import ink.ptms.fenestra.Fenestra
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.entity.Player

class InputBase(player: Player) : Input(player) {

    override fun next() {
        closeEvent[1] = true
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra")
            .rows(3)
            .items("", "    1    ")
            .put(
                '1', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(TLocale.asString(player, "workspace-generic-create"))
                    .build()
            )
            .click {
                if (it.slot == '1') {
                    createBaseNode()
                }
            }.close {
                if (closeEvent[1]!!) {
                    player.cancel()
                }
            }.open(player)
    }
}