// 依赖
val dependencyNames: Map<String, String> by rootProject.extra
dependencies {
    implementation         ("${dependencyNames["spigot"]}")
}
