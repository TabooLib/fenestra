package ink.ptms.fenestra

import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.SubCommand
import org.bukkit.entity.Player

/**
 * Fenestra
 * ink.ptms.fenestra.FenestraCommand
 *
 * @author sky
 * @since 2021/5/15 11:50 下午
 */
@BaseCommand(name = "fenestra", aliases = ["fe"], permission = "admin")
class FenestraCommand : BaseMainCommand() {

    @SubCommand(description = "@command-edit-description")
    fun edit(sender: Player, args: Array<String>) {
        FenestraAPI.edit(sender)
    }
}