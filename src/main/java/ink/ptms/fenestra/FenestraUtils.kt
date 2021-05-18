package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.toCompoundText
import io.izzel.taboolib.Version
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import io.izzel.taboolib.module.nms.nbt.NBTType
import io.izzel.taboolib.util.Coerce

fun NBTType.isListType() = when (this) {
    NBTType.BYTE_ARRAY, NBTType.INT_ARRAY, NBTType.LIST, NBTType.COMPOUND -> true
    else -> false
}

fun NBTType.createEmptyListData() = when (this) {
    NBTType.BYTE_ARRAY -> NBTBase(ByteArray(0))
    NBTType.INT_ARRAY -> NBTBase(IntArray(0))
    NBTType.LIST -> NBTList()
    NBTType.COMPOUND -> NBTCompound()
    else -> error("out of case")
}

fun NBTType.createGenericData(path: String, value: Any, origin: NBTBase? = null): NBTBase {
    val base = origin ?: NBTBase(0)
    when (this) {
        NBTType.BYTE -> base.reflex("data", Coerce.toByte(value))
        NBTType.SHORT -> base.reflex("data", Coerce.toShort(value))
        NBTType.INT -> base.reflex("data", Coerce.toInteger(value))
        NBTType.LONG -> base.reflex("data", Coerce.toLong(value))
        NBTType.FLOAT -> base.reflex("data", Coerce.toFloat(value))
        NBTType.DOUBLE -> base.reflex("data", Coerce.toDouble(value))
        NBTType.STRING -> {
            println(path)
            Thread.dumpStack()
            val str = if (path == "display.Name" || path == "display.Lore") {
                if (Version.isAfter(Version.v1_16)) {
                    TLocale.Translate.setColored(value.toString()).toCompoundText()
                } else {
                    TLocale.Translate.setColored(value.toString())
                }
            } else {
                value.toString()
            }
            base.reflex("data", str)
        }
        else -> {
        }
    }
    base.reflex("type", this)
    return base
}