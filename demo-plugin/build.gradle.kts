import org.apache.tools.ant.filters.ReplaceTokens

// 替换版本号
tasks.withType<ProcessResources> {
    from(sourceSets["main"].resources.srcDirs) {
        filter<ReplaceTokens>(("tokens" to mapOf("version" to version)))
    }
}

// 依赖
val dependencyNames: Map<String, String> by rootProject.extra
dependencies {
    compileOnly            ("${dependencyNames["bukkit"]}")
    annotationProcessor    ("${dependencyNames["lombok"]}")
    compileOnly            ("${dependencyNames["lombok"]}")
    compileOnly            (rootProject)
}
