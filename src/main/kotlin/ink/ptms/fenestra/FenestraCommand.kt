package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.createWorkspace
import ink.ptms.fenestra.FenestraAPI.toLegacyText
import ink.ptms.fenestra.FenestraAPI.workspace
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.module.nms.ItemTagType

/**
 * Fenestra
 * ink.ptms.fenestra.FenestraCommand
 *
 * @author sky
 * @since 2021/5/15 11:50 下午
 */
@CommandHeader(name = "fenestra", aliases = ["fe"], permission = "admin")
object FenestraCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val edit = subCommand {
        execute<Player> { sender, _, _ ->
            sender.createWorkspace()
        }
    }

    @CommandBody
    val info = subCommand {
        dynamic(commit = "console") {
            execute<Player> { sender, _, _ ->
                sender.createWorkspace(readMode = true, toConsole = true)
            }
        }
        execute<Player> { sender, _, _ ->
            sender.createWorkspace(readMode = true)
        }
    }

    @CommandBody
    val update = subCommand {
        dynamic(commit = "uuid") {
            dynamic(commit = "index") {
                execute<Player> { sender, context, index ->
                    val workspace = sender.workspace ?: return@execute
                    val uuid = context.argument(-1)
                    if (uuid == "create") {
                        WorkspaceInputs.nextBaseAction(sender)
                    } else {
                        val channel = workspace.channels[uuid] ?: return@execute
                        val nbt = channel.nbt
                        val idx = Coerce.toInteger(index)
                        when (nbt.type) {
                            ItemTagType.BYTE, ItemTagType.SHORT, ItemTagType.INT, ItemTagType.LONG, ItemTagType.FLOAT, ItemTagType.DOUBLE, ItemTagType.STRING -> {
                                workspace.updateState(nbt.asString().toLegacyText())
                                WorkspaceInputs.nextGenericAction(sender, channel)
                            }
                            ItemTagType.INT_ARRAY -> {
                                when (idx) {
                                    -1 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx, self = true)
                                    }
                                    -2 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx, create = true)
                                    }
                                    else -> {
                                        workspace.updateState(nbt.asIntArray()[idx])
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx)
                                    }
                                }
                            }
                            ItemTagType.BYTE_ARRAY -> {
                                when (idx) {
                                    -1 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx, byte = true, self = true)
                                    }
                                    -2 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx, byte = true, create = true)
                                    }
                                    else -> {
                                        workspace.updateState(nbt.asByteArray()[idx])
                                        WorkspaceInputs.nextArrayAction(sender, channel, idx, byte = true)
                                    }
                                }
                            }
                            ItemTagType.LIST -> {
                                when (idx) {
                                    -1 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextListAction(sender, channel, self = true)
                                    }
                                    -2 -> {
                                        workspace.updateState(channel.node)
                                        WorkspaceInputs.nextListAction(sender, channel, create = true)
                                    }
                                    else -> {
                                        val children = nbt.asList()[idx]
                                        when (children.type) {
                                            ItemTagType.BYTE, ItemTagType.SHORT, ItemTagType.INT, ItemTagType.LONG, ItemTagType.FLOAT, ItemTagType.DOUBLE, ItemTagType.STRING -> {
                                                workspace.updateState(children.asString().toLegacyText())
                                            }
                                            else -> {
                                                workspace.updateState("*")
                                            }
                                        }
                                        WorkspaceInputs.nextGenericAction(sender, workspace.getChannel(children)!!)
                                    }
                                }
                            }
                            ItemTagType.COMPOUND -> {
                                workspace.updateState(channel.node)
                                WorkspaceInputs.nextCompoundAction(sender, channel)
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }
}