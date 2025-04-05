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
    implementation(libs.jSystemThemeDetector)
    implementation(compose.materialIconsExtended)
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
