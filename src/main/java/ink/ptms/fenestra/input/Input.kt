package ink.ptms.fenestra.input

import ink.ptms.fenestra.FenestraAPI.workspace
import org.bukkit.entity.Player

/**
 * Fenestra
 * ink.ptms.fenestra.input.Input
 *
 * @author sky
 * @since 2021/5/18 5:32 下午
 */
interface Input {

    fun Player.cancel() {
        workspace?.run {
            cancelState()
            sendWorkspace()
        }
    }
}