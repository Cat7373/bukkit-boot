import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
}

group = "org.cat73"
version = "1.0.0-dev"

// Java 版本
configure<JavaPluginConvention> {
    val javaVersion = JavaVersion.VERSION_1_8

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

// 仓库配置
repositories {
    mavenLocal()
    jcenter()
    maven("https://hub.spigotmc.org/nexus/content/repositories/public")
}

tasks {
    // 源文件编码
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // 替换版本号
    withType<ProcessResources> {
        from(sourceSets["main"].resources.srcDirs) {
            filter<ReplaceTokens>(("tokens" to mapOf("version" to version)))
        }
    }
}

// 依赖版本
val bukkitVersion =             "1.13.2-R0.1-SNAPSHOT"
val lombokVersion =             "1.18.6"

// 依赖
dependencies {
    compileOnly            ("org.bukkit:bukkit:$bukkitVersion")
    annotationProcessor    ("org.projectlombok:lombok:$lombokVersion")
    compileOnly            ("org.projectlombok:lombok:$lombokVersion")
    compileOnly            (rootProject)
}
