package ink.ptms.fenestra.input

import ink.ptms.fenestra.*
import ink.ptms.fenestra.FenestraAPI.workspace
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.util.Features
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.entity.Player

/**
 * Fenestra
 * ink.ptms.fenestra.input.Input
 *
 * @author sky
 * @since 2021/5/18 5:32 下午
 */
abstract class Input(val player: Player) {

    protected val closeEvent = HashMap<Int, Boolean>()

    protected fun Player.cancel() {
        workspace?.run {
            cancelState()
            sendWorkspace()
        }
    }

    protected fun updateNode(channel: Channel, index: Int = -1, level: Int = 1) {
        closeEvent[level] = false
        Features.inputChat(player, object : Features.ChatInput {

            override fun quit() = "cancel;"

            override fun cancel() {
                next()
            }

            override fun onChat(message: String): Boolean {
                player.workspace?.updateChannel(channel, message, index)
                player.cancel()
                return false
            }
        })
        player.sendLocale("workspace-generic-input-data")
        player.closeInventory()
    }

    protected fun updateNodeType(channel: Channel, index: Int = -1, level: Int = 1) {
        closeEvent[level] = false
        WorkspaceInputs.nextType(player).thenAccept { type ->
            if (type == null) {
                Tasks.delay(1) {
                    next()
                }
            } else {
                // 新建列表类型，无需输入数据
                if (type.isListType()) {
                    player.workspace?.removeChannel(channel, index)
                    player.workspace?.createChannel(channel, type, channel.node)
                    player.cancel()
                }
                // 新建基本数据，需要输入内容
                else {
                    Features.inputChat(player, object : Features.ChatInput {

                        override fun quit() = "cancel;"

                        override fun cancel() {
                            next()
                        }

                        override fun onChat(message: String): Boolean {
                            player.workspace?.removeChannel(channel, index)
                            player.workspace?.createChannel(channel, type, channel.node, data = message)
                            player.cancel()
                            return false
                        }
                    })
                    player.sendLocale("workspace-generic-input-data")
                    player.closeInventory()
                }
            }
        }
    }

    protected fun updateNodeChoose(channel: Channel, index: Int = -1, level: Int = 1) {
        closeEvent[level] = false
        closeEvent[level + 1] = true
        MenuBuilder.builder(Fenestra.plugin)
            .title("Fenestra")
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
                    '1' -> updateNode(channel, index = index, level = level + 1)
                    '2' -> updateNodeType(channel, index = index, level = level + 1)
                }
            }.close {
                if (closeEvent[level + 1]!!) {
                    Tasks.delay(1) {
                        next()
                    }
                }
            }.open(player)
    }

    protected fun createNode(channel: Channel, type: NBTType, node: String? = null, children: Boolean = false) {
        // 新建列表类型，无需输入数据
        if (type.isListType()) {
            player.workspace?.createChannel(channel, type, node, children = children)
            player.cancel()
        }
        // 新建基本数据，需要输入内容
        else {
            Features.inputChat(player, object : Features.ChatInput {

                override fun quit() = "cancel;"

                override fun cancel() {
                    next()
                }

                override fun onChat(message: String): Boolean {
                    player.workspace?.createChannel(channel, type, node, message, children = children)
                    player.cancel()
                    return false
                }
            })
            player.sendLocale("workspace-generic-input-data")
        }
    }

    protected fun createNode(channel: Channel, level: Int = 1, children: Boolean = false) {
        closeEvent[level] = false
        // 输入节点类型
        WorkspaceInputs.nextType(player).thenAccept { type ->
            if (type == null) {
                Tasks.delay(1) {
                    next()
                }
            } else {
                // 在 List 类型中的任何数据都不需要节点名称
                if ((children && channel.nbt.type == NBTType.LIST) || channel.parent?.nbt?.type == NBTType.LIST) {
                    createNode(channel, type, children = children)
                }
                // 输入节点名称
                else {
                    Features.inputChat(player, object : Features.ChatInput {

                        override fun quit() = "cancel;"

                        override fun cancel() {
                            next()
                        }

                        override fun onChat(node: String): Boolean {
                            Tasks.delay(1) {
                                createNode(channel, type, node = node, children = children)
                            }
                            return false
                        }
                    })
                    player.sendLocale("workspace-generic-input-node")
                    player.closeInventory()
                }
            }
        }
    }

    protected fun createBaseNode(level: Int = 1) {
        closeEvent[level] = false
        // 输入节点类型
        WorkspaceInputs.nextType(player).thenAccept { type ->
            if (type == null) {
                Tasks.delay(1) {
                    next()
                }
            } else {
                Features.inputChat(player, object : Features.ChatInput {

                    override fun quit() = "cancel;"

                    override fun cancel() {
                        next()
                    }

                    override fun onChat(node: String): Boolean {
                        Tasks.delay(1) {
                            // 新建列表类型，无需输入数据
                            if (type.isListType()) {
                                player.workspace?.compound?.set(node, type.createEmptyListData())
                                player.cancel()
                            }
                            // 新建基本数据，需要输入内容
                            else {
                                Features.inputChat(player, object : Features.ChatInput {

                                    override fun quit() = "cancel;"

                                    override fun cancel() {
                                        next()
                                    }

                                    override fun onChat(message: String): Boolean {
                                        player.workspace?.compound?.set(node, type.createGenericData(node, message))
                                        player.cancel()
                                        return false
                                    }
                                })
                                player.sendLocale("workspace-generic-input-data")
                            }
                        }
                        return false
                    }
                })
                player.sendLocale("workspace-generic-input-node")
                player.closeInventory()
            }
        }
    }

    abstract fun next()
}