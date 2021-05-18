package ink.ptms.fenestra.input

import ink.ptms.fenestra.Fenestra
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.entity.Player

object InputBase : Input {

    fun next(player: Player) {
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
                when (it.slot) {
                    '1' -> {
                    }
                }
            }.close {
                player.cancel()
            }.open(player)
    }
}