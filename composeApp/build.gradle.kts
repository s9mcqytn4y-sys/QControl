import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

group = "id.primaraya.qcontrol"
version = "0.1.0"

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.koin.compose)
    implementation(libs.logback.classic)
    testImplementation(libs.junit)
}

compose.desktop {
    application {
        mainClass = "id.primaraya.qcontrol.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "QControl"
            packageVersion = "0.1.0"
            description = "Software desktop Quality Control internal."
            vendor = "Prima Raya"
            copyright = "© 2026 Prima Raya"
            
            windows {
                shortcut = true
                menu = true
                upgradeUuid = "c503f87d-88f8-4d6d-a8b8-906362618d93"
            }
        }
        
        jvmArgs("-Xms128m", "-Xmx1024m", "-XX:+UseG1GC")
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
