rootProject.name = "bukkit-boot"

/**
 * 导入子项目
 */
fun includeProjects() {
    println("start includeProjects.")

    rootProject.projectDir
            .listFiles { f -> f.isDirectory && File(f, "build.gradle.kts").exists() }
            .map { it.name }
            .forEach {
                println("include $it")
                include(":$it")
            }

    println("includeProjects complete.")
}

includeProjects()
