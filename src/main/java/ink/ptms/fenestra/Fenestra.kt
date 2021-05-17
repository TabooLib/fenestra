package ink.ptms.fenestra

import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject

object Fenestra : Plugin() {

    @TInject
    lateinit var conf: TConfig
        private set
}