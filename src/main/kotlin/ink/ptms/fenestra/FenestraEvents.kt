package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.cancelWorkspace
import ink.ptms.fenestra.FenestraAPI.inWorkspace
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang

/**
 * Fenestra
 * ink.ptms.fenestra.Events
 *
 * @author sky
 * @since 2021/5/17 11:38 下午
 */
object FenestraEvents {

    @Schedule(period = 20)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            if (it.inWorkspace) {
                it.sendLang("workspace-notify")
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
            e.player.cancelWorkspace(save = true)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerDropItemEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
            e.player.cancelWorkspace()
        }
    }

    @SubscribeEvent
    fun e(e: PlayerItemHeldEvent) {
        if (e.player.inWorkspace) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: InventoryClickEvent) {
        if ((e.whoClicked as Player).inWorkspace) {
            e.isCancelled = true
        }
    }
}