package ink.ptms.fenestra

import com.google.common.base.Strings
import ink.ptms.fenestra.FenestraAPI.toLegacyText
import io.izzel.taboolib.kotlin.getCompound
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

/**
 * Fenestra
 * ink.ptms.fenestra.Workspace
 *
 * @author sky
 * @since 2021/5/16 12:08 上午
 */
class Workspace(val player: Player, val itemStack: ItemStack, val isReadOnly: Boolean = false) {

    /**
     * 当前物品数据
     */
    val compound = itemStack.getCompound()

    /**
     * 当前工作通道
     */
    val channels = HashMap<String, Channel>()

    /**
     * 编辑状态显示
     */
    private val stateBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID)

    init {
        send()
    }

    /**
     * 将编辑视图发送给玩家
     */
    fun send() {
        channels.clear()
        if (!isReadOnly) {
            player.newWorkspace()
        }
        splitLine(true)
        if (compound.isEmpty()) {
            player.newJson {
                if (isReadOnly) {
                    append("§f{ }")
                } else {
                    append("§f{ }").clickCommand("/fenestra update create").hoverText("§7${TLocale.asString(player, "command-node-hover")}")
                }
            }
        } else {
            compound.forEach { (k, _) ->
                send(k, k)
            }
        }
        splitLine(false)
    }

    /**
     * 将缓存写入物品
     */
    fun save() {
        compound.saveTo(itemStack)
    }

    fun updateState(value: Any) {
        stateBar.setTitle(TLocale.asString(player, "workspace-state", value))
        stateBar.addPlayer(player)
    }

    fun cancelState() {
        stateBar.removePlayer(player)
    }

    private fun send(path: String, node: String, space: Int = 0) {
        if (node.contains(".")) {
            player.newJson { append(Strings.repeat("  ", space)).append("§c<$node>").hoverText("§cThis node is not writable.") }
        } else {
            player.newJson { append(path, node, compound.getDeep(path), space + 1, null) }
        }
    }

    private fun splitLine(newLine: Boolean) {
        player.newJson("") {
            if (newLine) {
                newLine()
            }
            append("§7§m${Strings.repeat(" ", Fenestra.conf.getInt("default.line-size"))}")
            append("§8「§fFenestra§8」")
            append("§7§m${Strings.repeat(" ", Fenestra.conf.getInt("default.line-size"))}")
        }
    }

    private fun Player.newWorkspace() = TellrawJson.create().also { json -> repeat(100) { json.newLine() } }.send(this)

    private fun Player.newJson(prefix: String = "  ", func: TellrawJson.() -> Unit) {
        TellrawJson.create().append(prefix).also(func).send(this)
    }

    private fun TellrawJson.editJson(nbt: NBTBase, path: String, node: String, parent: Channel?, index: Int = -1): TellrawJson {
        if (isReadOnly) {
            return this
        }
        val uuid = UUID.randomUUID().toString()
        channels[uuid] = Channel(nbt, path, node, parent)
        return clickCommand("/fenestra update $uuid $index").hoverText("§7${TLocale.asString(player, "command-node-hover")}")
    }

    private fun TellrawJson.append(path: String, node: String, nbt: NBTBase, space: Int, parent: Channel?, inList: Boolean = false): TellrawJson {
        when (nbt.type) {
            NBTType.STRING, NBTType.BYTE, NBTType.SHORT, NBTType.LONG, NBTType.FLOAT, NBTType.INT, NBTType.DOUBLE -> {
                if (!inList) {
                    append("§8$node: ")
                }
            }
            NBTType.BYTE_ARRAY, NBTType.INT_ARRAY, NBTType.COMPOUND, NBTType.LIST -> {
                if (!inList) {
                    append("§7$node: ").editJson(nbt, path, node, parent)
                }
            }
            else -> {
            }
        }
        when (nbt.type) {
            NBTType.STRING -> {
                append("§7'§f${nbt.asString().toLegacyText()}§7'").editJson(nbt, path, node, parent)
            }
            NBTType.BYTE, NBTType.SHORT, NBTType.LONG, NBTType.FLOAT, NBTType.INT, NBTType.DOUBLE -> {
                append("§f${nbt.asString()}§7${nbt.type.suffix()}").editJson(nbt, path, node, parent)
            }
            NBTType.LIST -> {
                val channel = getChannelFromNBT(nbt)
                nbt.asList().forEachIndexed { index, data ->
                    newLine().append("  ").append(repeat(inList, space, index)).append("- ").append(path, node, data, space, channel, true)
                }
                if (nbt.asList().isEmpty()) {
                    append("§f[ ]").editJson(nbt, path, node, parent, -2)
                }
            }
            NBTType.INT_ARRAY -> {
                nbt.asIntArray().forEachIndexed { index, data ->
                    newLine().append("  ").append(repeat(inList, space, index)).append("- §f${data}").editJson(nbt, path, node, parent, index)
                }
                if (nbt.asIntArray().isEmpty()) {
                    append("§f[ ]").editJson(nbt, path, node, parent, -2)
                }
            }
            NBTType.BYTE_ARRAY -> {
                nbt.asByteArray().forEachIndexed { index, data ->
                    newLine().append("  ").append(repeat(inList, space, index)).append("- §f${data}b").editJson(nbt, path, node, parent, index)
                }
                if (nbt.asByteArray().isEmpty()) {
                    append("§f[ ]").editJson(nbt, path, node, parent, -2)
                }
            }
            NBTType.COMPOUND -> {
                val channel = getChannelFromNBT(nbt)
                var i = 0
                nbt.asCompound().forEach { (key, data) ->
                    if (!inList || i > 0) {
                        newLine().append(Strings.repeat("  ", space + if (inList) 2 else 1))
                    }
                    append("$path.$key", key, data, space + 1, channel, false)
                    i++
                }
                if (i == 0) {
                    append("§f{ }").editJson(nbt, path, node, parent, -2)
                }
            }
            else -> {
            }
        }
        return this
    }

    private fun NBTType.suffix(): String {
        return when (this) {
            NBTType.BYTE -> "b"
            NBTType.SHORT -> "s"
            NBTType.LONG -> "L"
            NBTType.FLOAT -> "f"
            else -> ""
        }
    }

    private fun repeat(inList: Boolean, space: Int, index: Int): String {
        return if (inList && index > 0) {
            Strings.repeat("  ", space + 1)
        } else {
            Strings.repeat("  ", space)
        }
    }

    private fun getChannelFromNBT(nbt: NBTBase) = channels.values.firstOrNull { it.nbt === nbt }
}