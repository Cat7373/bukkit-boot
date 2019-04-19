import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
}

// ext
val bukkitVersion =             "1.13.2-R0.1-SNAPSHOT"
val lombokVersion =             "1.18.6"
val jsr305Version =             "3.0.2"
val junitVersion =              "5.4.1"
val dependencyNames = mapOf(
        "bukkit"               to "org.bukkit:bukkit:$bukkitVersion",
        "spigot"               to "org.spigotmc:spigot:$bukkitVersion",
        "lombok"               to "org.projectlombok:lombok:$lombokVersion",
        "jsr305"               to "com.google.code.findbugs:jsr305:$jsr305Version",
        "junit-jupiter-api"    to "org.junit.jupiter:junit-jupiter-api:$junitVersion",
        "junit-jupiter-engine" to "org.junit.jupiter:junit-jupiter-engine:$junitVersion"
)
extra["dependencyNames"] = dependencyNames

allprojects {
    apply(plugin = "org.gradle.java-library")

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

    // 源文件编码
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

// 替换版本号
tasks.withType<ProcessResources> {
    from(sourceSets["main"].resources.srcDirs) {
        filter<ReplaceTokens>(("tokens" to mapOf("version" to version)))
    }
}

// 使用 JUnit 的单元测试平台
tasks.withType<Test> {
    useJUnitPlatform()
}

// 依赖：compileOnly 的依赖注意 CraftBukkit 中要有
dependencies {
    compileOnly            ("${dependencyNames["bukkit"]}")
    compileOnly            ("${dependencyNames["jsr305"]}")
    annotationProcessor    ("${dependencyNames["lombok"]}")
    compileOnly            ("${dependencyNames["lombok"]}")

    testAnnotationProcessor ("${dependencyNames["lombok"]}")
    testCompileOnly         ("${dependencyNames["lombok"]}")
    testImplementation      ("${dependencyNames["junit-jupiter-api"]}")
    testRuntimeOnly         ("${dependencyNames["junit-jupiter-engine"]}")
}
