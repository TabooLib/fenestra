package ink.ptms.fenestra

import ink.ptms.fenestra.FenestraAPI.toCompoundText
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.nms.*

fun ItemTagType.isListType() = when (this) {
    ItemTagType.BYTE_ARRAY, ItemTagType.INT_ARRAY, ItemTagType.LIST, ItemTagType.COMPOUND -> true
    else -> false
}

fun ItemTagType.createEmptyListData() = when (this) {
    ItemTagType.BYTE_ARRAY -> ItemTagData(ByteArray(0))
    ItemTagType.INT_ARRAY -> ItemTagData(IntArray(0))
    ItemTagType.LIST -> ItemTagList()
    ItemTagType.COMPOUND -> ItemTag()
    else -> error("out of case")
}

fun ItemTagType.createGenericData(path: String, value: Any, origin: ItemTagData? = null): ItemTagData {
    val base = origin ?: ItemTagData(0)
    when (this) {
        ItemTagType.BYTE -> base.setProperty("data", Coerce.toByte(value))
        ItemTagType.SHORT -> base.setProperty("data", Coerce.toShort(value))
        ItemTagType.INT -> base.setProperty("data", Coerce.toInteger(value))
        ItemTagType.LONG -> base.setProperty("data", Coerce.toLong(value))
        ItemTagType.FLOAT -> base.setProperty("data", Coerce.toFloat(value))
        ItemTagType.DOUBLE -> base.setProperty("data", Coerce.toDouble(value))
        ItemTagType.STRING -> {
            val str = if (path in Fenestra.conf.getStringList("default.text-path")) {
                if (MinecraftVersion.majorLegacy >= 11600) {
                    value.toString().colored().toCompoundText()
                } else {
                    value.toString().colored()
                }
            } else {
                value.toString()
            }
            base.setProperty("data", str)
        }
        else -> {
        }
    }
    base.setProperty("type", this)
    return base
}