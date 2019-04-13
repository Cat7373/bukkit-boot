// 依赖
val dependencyNames: Map<String, String> by rootProject.extra
dependencies {
    compileOnly            ("${dependencyNames["bukkit"]}")
    annotationProcessor    ("${dependencyNames["lombok"]}")
    compileOnly            ("${dependencyNames["lombok"]}")
    compileOnly            (rootProject)
}
