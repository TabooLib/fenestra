package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.cancelWorkspace
import ink.ptms.fenestra.FenestraAPI.inWorkspace
import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

/**
 * Fenestra
 * ink.ptms.fenestra.Events
 *
 * @author sky
 * @since 2021/5/17 11:38 下午
 */
@TListener
class FenestraEvents : Listener {

    @TSchedule(period = 20)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            if (it.inWorkspace) {
                it.sendLocale("workspace-notify")
            }
        }
    }

    @EventHandler
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
            e.player.cancelWorkspace()
        }
    }

    @EventHandler
    fun e(e: PlayerItemHeldEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerDropItemEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: InventoryClickEvent) {
        if ((e.whoClicked as Player).inWorkspace) {
            e.isCancelled = true
        }
    }
}