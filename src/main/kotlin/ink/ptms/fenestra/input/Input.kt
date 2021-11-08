package ink.ptms.fenestra.input

import ink.ptms.fenestra.*
import ink.ptms.fenestra.FenestraAPI.workspace
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.ItemTagType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.nextChat
import taboolib.platform.util.sendLang

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
        player.nextChat {
            player.workspace?.updateChannel(channel, it, index)
            player.cancel()
        }
        player.sendLang("workspace-generic-input-data")
        player.closeInventory()
    }

    protected fun updateNodeType(channel: Channel, index: Int = -1, level: Int = 1) {
        closeEvent[level] = false
        WorkspaceInputs.nextType(player).thenAccept { type ->
            if (type == null) {
                submit(delay = 1) { next() }
            } else {
                // 新建列表类型，无需输入数据
                if (type.isListType()) {
                    player.workspace?.removeChannel(channel, index)
                    player.workspace?.createChannel(channel, type, channel.node)
                    player.cancel()
                }
                // 新建基本数据，需要输入内容
                else {
                    player.nextChat {
                        player.workspace?.removeChannel(channel, index)
                        player.workspace?.createChannel(channel, type, channel.node, data = it)
                        player.cancel()
                    }
                    player.sendLang("workspace-generic-input-data")
                    player.closeInventory()
                }
            }
        }
    }

    protected fun updateNodeChoose(channel: Channel, index: Int = -1, level: Int = 1) {
        closeEvent[level] = false
        closeEvent[level + 1] = true
        player.openMenu<Basic>("Fenestra") {
            rows(3)
            map("", "   1 2   ")
            set(
                '1', ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .name(player.asLangText("workspace-generic-update-data"))
                    .build()
            )
            set(
                '2', ItemBuilder(XMaterial.PAPER)
                    .name(player.asLangText("workspace-generic-update-type"))
                    .build()
            )
            onClick { c2 ->
                when (c2.slot) {
                    '1' -> updateNode(channel, index = index, level = level + 1)
                    '2' -> updateNodeType(channel, index = index, level = level + 1)
                }
            }
            onClose {
                if (closeEvent[level + 1]!!) {
                    submit(delay = 1) { next() }
                }
            }
        }
    }

    protected fun createNode(channel: Channel, type: ItemTagType, node: String? = null, children: Boolean = false) {
        // 新建列表类型，无需输入数据
        if (type.isListType()) {
            player.workspace?.createChannel(channel, type, node, children = children)
            player.cancel()
        }
        // 新建基本数据，需要输入内容
        else {
            player.nextChat {
                player.workspace?.createChannel(channel, type, node, it, children = children)
                player.cancel()
            }
            player.sendLang("workspace-generic-input-data")
        }
    }

    protected fun createNode(channel: Channel, level: Int = 1, children: Boolean = false) {
        closeEvent[level] = false
        // 输入节点类型
        WorkspaceInputs.nextType(player).thenAccept { type ->
            if (type == null) {
                submit(delay = 1) { next() }
            } else {
                // 在 List 类型中的任何数据都不需要节点名称
                if ((children && channel.nbt.type == ItemTagType.LIST) || (!children && channel.parent?.nbt?.type == ItemTagType.LIST)) {
                    createNode(channel, type, children = children)
                }
                // 输入节点名称
                else {
                    player.nextChat {
                        submit(delay = 1) { createNode(channel, type, node = it, children = children) }
                    }
                    player.sendLang("workspace-generic-input-node")
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
                submit(delay = 1) { next() }
            } else {
                player.nextChat { node ->
                    submit(delay = 1) {
                        // 新建列表类型，无需输入数据
                        if (type.isListType()) {
                            player.workspace?.compound?.set(node, type.createEmptyListData())
                            player.cancel()
                        }
                        // 新建基本数据，需要输入内容
                        else {
                            player.nextChat { message ->
                                player.workspace?.compound?.set(node, type.createGenericData(node, message))
                                player.cancel()
                            }
                            player.sendLang("workspace-generic-input-data")
                        }
                    }
                    player.sendLang("workspace-generic-input-node")
                    player.closeInventory()
                }
            }
        }
    }

    abstract fun next()
}