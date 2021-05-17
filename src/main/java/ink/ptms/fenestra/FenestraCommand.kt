package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.createWorkspace
import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.SubCommand
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.util.Coerce
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
        sender.createWorkspace()
    }

    @SubCommand(hideInHelp = true)
    fun update(sender: Player, args: Array<String>) {
        val workspace = sender.workspace ?: return
        val nbt = workspace.channels[args[0]] ?: return
        val index = Coerce.toInteger(args[1])
        when (nbt.type) {
            NBTType.BYTE, NBTType.SHORT, NBTType.INT, NBTType.LONG, NBTType.FLOAT, NBTType.DOUBLE, NBTType.STRING -> {
                WorkspaceInputs.nextGenericAction(sender, nbt)
            }
            NBTType.INT_ARRAY -> {
                when (index) {
                    -1 -> WorkspaceInputs.nextArrayAction(sender, nbt, index, self = true)
                    -2 -> WorkspaceInputs.nextArrayAction(sender, nbt, index, create = true)
                    else -> WorkspaceInputs.nextArrayAction(sender, nbt, index)
                }
            }
            NBTType.BYTE_ARRAY -> {
                when (index) {
                    -1 -> WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true, self = true)
                    -2 -> WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true, create = true)
                    else -> WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true)
                }
            }
            NBTType.LIST -> {
                if (index == -2) {
                    println("edit 4 new list")
                } else {
                    println("edit 4 edit list")
                }
            }
            NBTType.COMPOUND -> {
                if (index == -2) {
                    println("edit 5 new compound")
                } else {
                    println("edit 5 edit compound")
                }
            }
            else -> {
            }
        }
    }
}