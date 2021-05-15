package ink.ptms.fenestra.structure

import io.izzel.taboolib.kotlin.getCompound
import org.bukkit.inventory.ItemStack

/**
 * Fenestra
 * ink.ptms.fenestra.structure.Workspace
 *
 * @author sky
 * @since 2021/5/16 12:08 上午
 */
class Workspace(val itemStack: ItemStack) {

    val compound = itemStack.getCompound()

    /**
     * 将编辑视图发送给玩家
     */
    fun send() {

    }
}