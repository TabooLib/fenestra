package ink.ptms.fenestra

import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object Fenestra : Plugin() {

    @Config
    lateinit var conf: Configuration
        private set
}