package ink.ptms.fenestra

import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

class ItemBuilder(val material: XMaterial) {

    var name: String? = null
    val lore = ArrayList<String>()
    var shiny = false
    var colored = false

    fun name(name: String): ItemBuilder {
        this.name = name
        return this
    }

    fun lore(vararg lore: String): ItemBuilder {
        this.lore += lore
        return this
    }

    fun lore(lore: List<String>): ItemBuilder {
        this.lore += lore
        return this
    }

    fun shiny(): ItemBuilder {
        this.shiny = true
        return this
    }

    fun colored(): ItemBuilder {
        this.colored = true
        return this
    }

    fun build(): ItemStack {
        return buildItem(material) {
            this.name = this@ItemBuilder.name
            this.lore += this@ItemBuilder.lore
            if (shiny) {
                shiny()
            }
            if (colored) {
                colored()
            }
        }
    }
}