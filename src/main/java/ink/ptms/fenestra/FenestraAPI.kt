package ink.ptms.fenestra

import io.izzel.taboolib.Version
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.chat.TextComponent
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

object FenestraAPI {

    @PlayerContainer
    private val workspace = ConcurrentHashMap<String, Workspace>()

    /**
     * 获取玩家工作空间
     */
    val Player.workspace: Workspace?
        get() = FenestraAPI.workspace[name]

    /**
     * 玩家是否在编辑模式中
     */
    val Player.inWorkspace: Boolean
        get() = FenestraAPI.workspace.containsKey(name)

    /**
     * 进入编辑模式
     */
    fun Player.createWorkspace() {
        val itemInMainHand = inventory.itemInMainHand
        if (Items.isNull(itemInMainHand)) {
            sendLocale("edit-item-air")
            return
        }
        FenestraAPI.workspace[name] = Workspace(this, itemInMainHand)
    }

    /**
     * 退出编辑模式
     */
    fun Player.cancelWorkspace() {
        FenestraAPI.workspace.remove(name)?.also {
            TellrawJson.create().also { json -> repeat(100) { json.newLine() } }.send(this)
            sendLocale("workspace-cancel")
        }
    }

    /**
     * 将超文本转换为可读文本
     */
    fun String.toLegacyText(): String {
        return if (Version.isAfter(Version.v1_16)) {
            try {
                TextComponent.toLegacyText(*ComponentSerializer.parse(this))
            } catch (ex: Exception) {
                this
            }
        } else {
            this
        }
    }
}