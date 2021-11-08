package ink.ptms.fenestra

import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.isAir
import taboolib.platform.util.sendLang
import java.util.concurrent.ConcurrentHashMap

object FenestraAPI {

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
    fun Player.createWorkspace(readMode: Boolean = false) {
        val itemInMainHand = inventory.itemInMainHand
        if (itemInMainHand.isAir()) {
            sendLang("command-edit-item-air")
            return
        }
        if (readMode) {
            Workspace(this, itemInMainHand, true)
        } else {
            FenestraAPI.workspace[name] = Workspace(this, itemInMainHand)
        }
    }

    /**
     * 退出编辑模式
     */
    fun Player.cancelWorkspace(save: Boolean = false) {
        FenestraAPI.workspace.remove(name)?.also {
            TellrawJson().also { json -> repeat(100) { json.newLine() } }.sendTo(adaptPlayer(this))
            if (save) {
                it.saveWorkspace()
                sendLang("workspace-cancel-and-save")
            } else {
                sendLang("workspace-cancel")
            }
        }
    }

    /**
     * 将超文本转换为可读文本
     */
    fun String.toLegacyText(): String {
        return if (MinecraftVersion.majorLegacy >= 11600) {
            try {
                TextComponent.toLegacyText(*ComponentSerializer.parse(this))
            } catch (ex: Exception) {
                this
            }
        } else {
            this
        }
    }

    fun String.toCompoundText(): String {
        return ComponentSerializer.toString(*TextComponent.fromLegacyText(this).also {
            if (it[0].colorRaw != null) {
                it[0].isItalic = false
            }
        })
    }

    @SubscribeEvent
    internal fun e(e: PlayerQuitEvent) {
        workspace.remove(e.player.name)
    }
}