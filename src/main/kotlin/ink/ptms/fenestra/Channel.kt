package ink.ptms.fenestra

import taboolib.module.nms.ItemTagData

/**
 * Fenestra
 * ink.ptms.fenestra.Channel
 *
 * @author sky
 * @since 2021/5/18 12:45 下午
 */
class Channel(val nbt: ItemTagData, val path: String, node: String, val parent: Channel?) {

    val node = if (node.contains('.')) {
        node.substring(node.lastIndexOf('.') + 1)
    } else {
        node
    }
}