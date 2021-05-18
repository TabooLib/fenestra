package ink.ptms.fenestra.input

import ink.ptms.fenestra.Channel
import ink.ptms.fenestra.Fenestra
import ink.ptms.fenestra.FenestraAPI.workspace
import ink.ptms.fenestra.WorkspaceInputs
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Features
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
object InputGeneric : Input {

    fun next(player: Player, channel: Channel) {
        var closeLogic = true
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
                        closeLogic = false
                        MenuBuilder.builder(Fenestra.plugin)
                            .title("Fenestra Generic")
                            .rows(3)
                            .items("", "   1 2   ")
                            .put(
                                '1', ItemBuilder(XMaterial.WRITABLE_BOOK)
                                    .name(TLocale.asString(player, "workspace-generic-update-data"))
                                    .build()
                            )
                            .put(
                                '2', ItemBuilder(XMaterial.PAPER)
                                    .name(TLocale.asString(player, "workspace-generic-update-type"))
                                    .build()
                            ).click { c2 ->
                                when (c2.slot) {
                                    '1' -> {
                                        Features.inputChat(player, object : Features.ChatInput {

                                            override fun quit() = "cancel;"

                                            override fun cancel() {
                                                player.openInventory(c1.inventory)
                                            }

                                            override fun onChat(message: String): Boolean {
                                                player.workspace?.updateChannel(channel, message)
                                                player.cancel()
                                                return false
                                            }
                                        })
                                        player.sendLocale("workspace-generic-input")
                                        player.closeInventory()
                                    }
                                    '2' -> {
                                        WorkspaceInputs.nextType(player).thenAccept {

                                        }
                                    }
                                }
                            }.close {
                                Tasks.delay(1) {
                                    player.openInventory(c1.inventory)
                                }
                            }.open(player)
                    }
                    '3' -> {

                    }
                }
            }.close {
                if (closeLogic) {
                    player.cancel()
                }
            }.open(player)
    }
}