rootProject.name = "bukkit-boot-root"

/**
 * 导入子项目
 */
fun includeProjects() {
    println("start includeProjects.")

    // subProjects
    rootProject.projectDir
            .listFiles { f -> f.isDirectory && File(f, "build.gradle.kts").exists() }
            .map { ":${it.name}" }
            .forEach {
                println("include $it")
                include(it)
            }

    // examples
    File(rootProject.projectDir, "examples")
            .listFiles { f -> f.isDirectory && File(f, "build.gradle.kts").exists() }
            .map { ":examples:${it.name}" }
            .forEach {
                println("include $it")
                include(it)
            }

    println("includeProjects complete.")
}

includeProjects()
