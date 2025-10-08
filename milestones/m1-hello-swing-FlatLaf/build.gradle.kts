plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.beryx.jlink") version "3.1.3"
    id("com.diffplug.spotless") version "8.0.0"
    id("com.github.spotbugs") version "6.2.5"
}

group = "de.bht.pr.quizzr"
version = "1.0"

repositories { mavenCentral() }

val junitVersion = "5.12.1"
val junitPlatformVersion = "1.12.1"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

application {
    // Modular setup to support jlink/jpackage
    mainModule.set("de.bht.pr.quizzr.swing")
    mainClass.set("de.bht.pr.quizzr.swing.SwingApp")
}

dependencies {
    implementation("com.formdev:flatlaf:3.4.1")
    implementation("com.formdev:flatlaf-intellij-themes:3.4.1")
    implementation("com.formdev:flatlaf-intellij-themes:3.6.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:${junitPlatformVersion}")
}

tasks.withType<Test> { useJUnitPlatform() }

spotless { java { googleJavaFormat(); target("src/**/*.java") } }

spotbugs { ignoreFailures.set(true) }

tasks.named("check") { dependsOn("spotbugsMain", "spotbugsTest", "spotlessCheck") }

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${org.gradle.internal.os.OperatingSystem.current().toString().lowercase()}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher { name = "quizzr-swing" }
    jpackage {
        val os = org.gradle.internal.os.OperatingSystem.current()
        if (os.isMacOsX) {
            skipInstaller = true // produce only .app
        }
        if (os.isWindows) {
            installerType = "exe" // no .msi
        }
        imageName = "quizzr-swing"
        appVersion = "1.0.0"
    }
}
