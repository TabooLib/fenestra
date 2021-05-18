package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.createWorkspace
import ink.ptms.fenestra.FenestraAPI.toLegacyText
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

    @SubCommand(description = "@command-info-description")
    fun info(sender: Player, args: Array<String>) {
        sender.createWorkspace(true)
    }

    @SubCommand(hideInHelp = true)
    fun update(sender: Player, args: Array<String>) {
        val workspace = sender.workspace ?: return
        if (args[0] == "create") {
            WorkspaceInputs.nextBaseAction(sender)
        } else {
            val channel = workspace.channels[args[0]] ?: return
            val nbt = channel.nbt
            val index = Coerce.toInteger(args[1])
            when (nbt.type) {
                NBTType.BYTE, NBTType.SHORT, NBTType.INT, NBTType.LONG, NBTType.FLOAT, NBTType.DOUBLE, NBTType.STRING -> {
                    workspace.updateState(nbt.asString().toLegacyText())
                    WorkspaceInputs.nextGenericAction(sender, nbt)
                }
                NBTType.INT_ARRAY -> {
                    when (index) {
                        -1 -> {
                            workspace.updateState(channel.node)
                            WorkspaceInputs.nextArrayAction(sender, nbt, index, self = true)
                        }
                        -2 -> {
                            workspace.updateState(channel.node)
                            WorkspaceInputs.nextArrayAction(sender, nbt, index, create = true)
                        }
                        else -> {
                            workspace.updateState(nbt.asIntArray()[index])
                            WorkspaceInputs.nextArrayAction(sender, nbt, index)
                        }
                    }
                }
                NBTType.BYTE_ARRAY -> {
                    when (index) {
                        -1 -> {
                            workspace.updateState(channel.node)
                            WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true, self = true)
                        }
                        -2 -> {
                            workspace.updateState(channel.node)
                            WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true, create = true)
                        }
                        else -> {
                            workspace.updateState(nbt.asByteArray()[index])
                            WorkspaceInputs.nextArrayAction(sender, nbt, index, byte = true)
                        }
                    }
                }
                NBTType.LIST -> {
                    if (index < 0) {
                        workspace.updateState(channel.node)
                        WorkspaceInputs.nextListAction(sender, nbt)
                    } else {
                        val children = nbt.asList()[index]
                        when (children.type) {
                            NBTType.BYTE, NBTType.SHORT, NBTType.INT, NBTType.LONG, NBTType.FLOAT, NBTType.DOUBLE, NBTType.STRING -> {
                                workspace.updateState(children.asString().toLegacyText())
                            }
                            else -> {
                                workspace.updateState("*")
                            }
                        }
                        WorkspaceInputs.nextGenericAction(sender, children)
                    }
                }
                NBTType.COMPOUND -> {
                    workspace.updateState(channel.node)
                    WorkspaceInputs.nextCompoundAction(sender, nbt)
                }
                else -> {
                }
            }
        }
    }
}