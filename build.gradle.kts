import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "dev.secondsun.tools"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven ( "https://jitpack.io" )
    mavenLocal()
}


dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.desktop.common)
    implementation(compose.components.resources)

    implementation(libs.reorderable)
    implementation(libs.jvm.opencv)
    implementation(libs.filekit.compose) {exclude(group = " net.java.dev.jna", module = "jna")}

    api(libs.datastore.preferences)
    api(libs.datastore)
    implementation(libs.jetbrains.lifecycle)
    //implementation(libs.jSystemThemeDetector)
    implementation(compose.materialIconsExtended)

// jSystemThemeDetector dependencies
    implementation ("org.slf4j:slf4j-api:1.7.32")
    //JNA
    implementation ("net.java.dev.jna:jna-jpms:5.17.0")
    implementation ("net.java.dev.jna:jna-platform-jpms:5.17.0")

    //JFA
    implementation ("de.jangassen:jfa:1.2.0") {exclude(group = "net.java.dev.jna", module = "jna")}

    //OSHI
    implementation ("com.github.oshi:oshi-core:5.8.6")

    implementation ("io.github.g00fy2:versioncompare:1.4.1")

    implementation ("org.jetbrains:annotations:22.0.0")

}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "rotoscope-poc"
            packageVersion = "1.0.0"
        }
    }

}

