import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    signing
    `maven-publish`
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
val dependencyNames: Map<String, String> by rootProject.extra
dependencies {
    compileOnly             ("${dependencyNames["bukkit"]}")
    compileOnly             ("${dependencyNames["jsr305"]}")
    annotationProcessor     ("${dependencyNames["lombok"]}")
    compileOnly             ("${dependencyNames["lombok"]}")

    testAnnotationProcessor ("${dependencyNames["lombok"]}")
    testCompileOnly         ("${dependencyNames["lombok"]}")
    testImplementation      ("${dependencyNames["junit-jupiter-api"]}")
    testRuntimeOnly         ("${dependencyNames["junit-jupiter-engine"]}")
}
