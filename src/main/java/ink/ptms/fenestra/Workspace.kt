package ink.ptms.fenestra

import com.google.common.base.Strings
import ink.ptms.fenestra.FenestraAPI.toLegacyText
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.kotlin.getCompound
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.Coerce
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
        sendWorkspace()
    }

    /**
     * 将编辑视图发送给玩家
     * 若工作空间仅可读则无法编辑数据
     */
    fun sendWorkspace() {
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
            compound.keys.forEach { player.newJson { appendJson(it, it, compound[it]!!, 1, null) } }
        }
        splitLine(false)
    }

    /**
     * 将缓存写入物品
     */
    fun saveWorkspace() {
        compound.saveTo(itemStack)
    }

    /**
     * 更新玩家的编辑状态显示
     * 头顶血条效果
     */
    fun updateState(value: Any) {
        stateBar.setTitle(TLocale.asString(player, "workspace-state", value))
        stateBar.addPlayer(player)
    }

    /**
     * 取消玩家的编辑状态显示
     */
    fun cancelState() {
        stateBar.removePlayer(player)
    }

    /**
     * 通过 NBTBase 获取工作通道
     */
    fun getChannel(nbt: NBTBase): Channel? {
        return channels.values.firstOrNull { it.nbt === nbt }
    }

    /**
     * 更新工作通道（从数据结构中更新数据）
     */
    fun updateChannel(channel: Channel, value: Any, index: Int = -1) {
        if (index != -1) {
            if (channel.nbt.type == NBTType.INT_ARRAY) {
                channel.nbt.asIntArray()[index] = Coerce.toInteger(value)
            } else if (channel.nbt.type == NBTType.BYTE_ARRAY) {
                channel.nbt.asByteArray()[index] = Coerce.toByte(value)
            }
        } else {
            channel.nbt.type.createGenericData(channel.path, value, channel.nbt)
        }
    }

    /**
     * 新增数据
     */
    fun createChannel(channel: Channel, type: NBTType, node: String? = null, data: Any? = null, children: Boolean = false) {
        when {
            children -> {
                writeNBT("${channel.path}${if (node == null) "" else ".$node"}", channel.nbt, type, node, data)
            }
            channel.parent == null -> {
                compound[node] = when {
                    type.isListType() -> type.createEmptyListData()
                    data != null -> type.createGenericData(node.toString(), data)
                    else -> return
                }
            }
            else -> {
                writeNBT(channel.path, channel.parent.nbt, type, node, data)
            }
        }
    }

    /**
     * 删除工作通道（从数据结构中移除）
     */
    fun removeChannel(channel: Channel, index: Int = -1) {
        if (channel.parent == null) {
            compound.remove(channel.path)
        } else {
            val parent = channel.parent.nbt
            when (parent.type) {
                NBTType.COMPOUND -> {
                    parent.asCompound().remove(channel.node)
                }
                NBTType.LIST -> {
                    parent.asList().remove(channel.nbt)
                }
                NBTType.BYTE_ARRAY -> {
                    val array = parent.asByteArray().toMutableList()
                    array.removeAt(index)
                    parent.reflex("data", array.toByteArray())
                }
                NBTType.INT_ARRAY -> {
                    val array = parent.asIntArray().toMutableList()
                    array.removeAt(index)
                    parent.reflex("data", array.toIntArray())
                }
                else -> {
                }
            }
        }
    }

    private fun Player.newWorkspace() = TellrawJson.create().also { json -> repeat(100) { json.newLine() } }.send(this)

    private fun Player.newJson(prefix: String = "  ", func: TellrawJson.() -> Unit) {
        TellrawJson.create().append(prefix).also(func).send(this)
    }

    private fun TellrawJson.editJson(nbt: NBTBase, path: String, node: String, parent: Channel?, index: Int = -1): TellrawJson {
        val source = if (nbt.type == NBTType.STRING) "\n§f${nbt.asString()}" else ""
        if (isReadOnly) {
            hoverText("§7$path$source")
            return this
        }
        val uuid = UUID.randomUUID().toString()
        val channel = Channel(nbt, path, node, parent)
        channels[uuid] = channel
        return clickCommand("/fenestra update $uuid $index").hoverText("§7${TLocale.asString(player, "command-node-hover")}\n§8$path$source")
    }

    private fun TellrawJson.appendJson(path: String, node: String, nbt: NBTBase, space: Int, parent: Channel?, inList: Boolean = false): TellrawJson {
        if (node.contains('.')) {
            append("§c<$node>").hoverText(TLocale.asString(player, "workspace-not-edit"))
            return this
        }
        when (nbt.type) {
            NBTType.STRING, NBTType.BYTE, NBTType.SHORT, NBTType.LONG, NBTType.FLOAT, NBTType.INT, NBTType.DOUBLE -> {
                if (!inList) {
                    append("§8$node: ")
                }
            }
            NBTType.BYTE_ARRAY, NBTType.INT_ARRAY, NBTType.COMPOUND, NBTType.LIST -> {
                if (!inList) {
                    append("§7$node: ").editJson(nbt, path, node, parent)
                } else {
                    channels[UUID.randomUUID().toString()] = Channel(nbt, path, node, parent)
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
                val channel = getChannel(nbt)
                nbt.asList().forEachIndexed { index, data ->
                    if (!inList || index > 0) {
                        newLine().append(repeat(inList, space + 1, index))
                    }
                    append("- ").editJson(data, path, node, channel, index).appendJson(path, node, data, space, channel, true)
                }
                if (nbt.asList().isEmpty()) {
                    append("§f[ ]").editJson(nbt, path, node, parent, -2).append(" §7(Any)")
                }
            }
            NBTType.INT_ARRAY -> {
                val channel = getChannel(nbt)
                nbt.asIntArray().forEachIndexed { index, data ->
                    if (!inList || index > 0) {
                        newLine().append(repeat(inList, space + 1, index))
                    }
                    append("- §f${data}").editJson(nbt, path, node, channel, index)
                }
                if (nbt.asIntArray().isEmpty()) {
                    append("§f[ ]").editJson(nbt, path, node, parent, -2).append(" §7(Int)")
                }
            }
            NBTType.BYTE_ARRAY -> {
                append("§8${nbt.asByteArray().size} Bytes").editJson(nbt, path, node, parent, -2).hoverText(TLocale.asString(player, "workspace-not-edit"))
//                nbt.asByteArray().forEachIndexed { index, data ->
//                    if (!inList || index > 0) {
//                        newLine().append(repeat(inList, space + 1, index))
//                    }
//                    append("- §f${data}§7b").editJson(nbt, path, node, channel, index)
//                }
//                if (nbt.asByteArray().isEmpty()) {
//                    append("§f[ ]").editJson(nbt, path, node, parent, -2).append(" §7(Byte)")
//                }
            }
            NBTType.COMPOUND -> {
                val channel = getChannel(nbt)
                var i = 0
                nbt.asCompound().forEach { (key, data) ->
                    if (!inList || i > 0) {
                        newLine().append(Strings.repeat("  ", space + if (inList) 2 else 1))
                    }
                    appendJson("$path.$key", key, data, space + 1, channel, false)
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

    private fun writeNBT(path: String, base: NBTBase, type: NBTType, node: String?, data: Any?) {
        when (base.type) {
            NBTType.COMPOUND -> {
                base.asCompound()[node] = when {
                    type.isListType() -> type.createEmptyListData()
                    data != null -> type.createGenericData(path, data)
                    else -> return
                }
            }
            NBTType.LIST -> {
                base.asList().add(
                    when {
                        type.isListType() -> type.createEmptyListData()
                        data != null -> type.createGenericData(path, data)
                        else -> return
                    }
                )
            }
            NBTType.INT_ARRAY -> {
                val array = base.asIntArray().toMutableList()
                array.add(Coerce.toInteger(data))
                base.reflex("data", array.toIntArray())
            }
            NBTType.BYTE_ARRAY -> {
                val array = base.asByteArray().toMutableList()
                array.add(Coerce.toByte(data))
                base.reflex("data", array.toByteArray())
            }
            else -> {
            }
        }
    }
}