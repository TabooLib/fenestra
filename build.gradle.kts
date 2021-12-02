plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("坏黑")
        }
    }
    install("common")
    install("common-5")
    install("module-ui")
    install("module-nms")
    install("module-nms-util")
    install("module-configuration")
    install("module-chat")
    install("module-lang")
    install("platform-bukkit")
    install("expansion-command-helper")
    classifier = null
    version = "6.0.6-8"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11600:11600-minimize")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo2s.ptms.ink/repository/maven-releases/")
            credentials {
                username = project.findProperty("user").toString()
                password = project.findProperty("password").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = "ink.ptms"
        }
    }
}