plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.3"
    id("com.diffplug.spotless") version "8.0.0"
    id("com.github.spotbugs") version "6.2.5"
}

group = "de.bht.pr.quizzr"
version = "1.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"
val junitPlatformVersion = "1.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("de.bht.pr.quizzr")
    mainClass.set("de.bht.pr.quizzr.QuizzrApplication")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing", "javafx.media")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("net.synedra:validatorfx:0.6.1") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.9") {
        exclude(group = "org.openjfx")
    }
    implementation("com.github.almasb:fxgl:17.3") {
        // Use JavaFX modules provided by the plugin
        exclude(group = "org.openjfx")
        // Keep Kotlin stdlib transitively; fxgl requires it for modules
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:${junitPlatformVersion}")
}


tasks.withType<Test> {
    useJUnitPlatform()
}

// Formatting: Google Java Format via Spotless
spotless {
    java {
        googleJavaFormat()
        target("src/**/*.java")
    }
}

// Static analysis: SpotBugs report-only to avoid blocking students
spotbugs {
    ignoreFailures.set(true)
}

// Ensure check runs code format and static analysis
tasks.named("check") {
    dependsOn("spotbugsMain", "spotbugsTest", "spotlessCheck")
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "quizzr"
    }

    jpackage {
        val os = org.gradle.internal.os.OperatingSystem.current()
        // Keep only the .app bundle on macOS (no .dmg/.pkg)
        if (os.isMacOsX) {
            skipInstaller = true
        }
        // Produce only .exe on Windows (no .msi)
        if (os.isWindows) {
            installerType = "exe"
        }
        imageName = "quizzr"
        appVersion = "1.0.0"
        jvmArgs = listOf("-p", ".")
    }
}
