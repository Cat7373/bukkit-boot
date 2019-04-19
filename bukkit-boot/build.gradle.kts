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

// ===== 发布相关 =====
// GPG Sign
extra["signing.keyId"] = System.getProperty("gpg.keyId")
extra["signing.password"] = System.getProperty("gpg.password")
extra["signing.secretKeyRingFile"] = System.getProperty("gpg.secretKeyRingFile")

// 源码 jar 包(用于发布到 Maven 库)
val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets["main"].allJava)
}

// JavaDoc jar 包配置
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    isFailOnError = false // TODO remove

    (options as? StandardJavadocDocletOptions)?.also {
        it.charSet = "UTF-8"
        it.isAuthor = true
        it.isVersion = true
        it.links = listOf("https://docs.oracle.com/javase/8/docs/api")
        if (JavaVersion.current().isJava9Compatible) {
            it.addBooleanOption("html5", true)
        }
    }
}

// JavaDoc 包(用于发布到 Maven 库)
val javadocJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}

// 发布到 Maven 库
publishing {
    repositories {
        maven {
            // 目标仓库
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            setUrl(if (version.toString().endsWith("RELEASE")) releasesRepoUrl else snapshotsRepoUrl)

            // 登录凭据，从启动参数中读取，避免直接把密码暴漏在源代码中
            credentials {
                username = System.getProperty("nexus.username")
                password = System.getProperty("nexus.password")
            }
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            pom {
                name.set("bukkit-boot")
                description.set("简化你的 Bukkit 插件开发")
                inceptionYear.set("2019")
                url.set("https://github.com/Cat7373/bukkit-boot")

                artifactId = project.name
                groupId = "${project.group}"
                version = "${project.version}"
                packaging = "jar"

                scm {
                    connection.set("scm:git@github.com:Cat7373/bukkit-boot.git")
                    developerConnection.set("scm:git@github.com:Cat7373/bukkit-boot.git")
                    url.set("https://github.com/Cat7373/bukkit-boot")
                }

                issueManagement {
                    url.set("https://github.com/Cat7373/bukkit-boot/issues")
                }

                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://raw.githubusercontent.com/Cat7373/bukkit-boot/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("Cat73")
                        name.set("Cat73")
                        email.set("root@cat73.org")
                        url.set("https://github.com/Cat7373")
                        timezone.set("Asia/Shanghai")
                    }
                }
            }

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }

    signing {
        sign(publishing.publications.getByName("mavenJava"))
    }
}
