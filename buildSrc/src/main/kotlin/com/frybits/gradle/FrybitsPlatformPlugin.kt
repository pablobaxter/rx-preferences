package com.frybits.gradle

import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

private val FRYBITS_LIBRARY_REGEX = "^plugins \\{(?s).*\\n\\s+id ('frybits-library'|\"frybits-library\")(?s).*}\$".toRegex()

class FrybitsPlatformPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlatformPlugin()
        target.apply<MavenPublishPlugin>()

    }
}

private fun Project.applyPlatformPlugin() {
    apply<JavaPlatformPlugin>()

    // Read all the build files directly, to prevent subproject configuration.
    rootProject.subprojects.filter { p ->
        // This assumes that all projects with the "frybits-library" plugin should be added to BOM
        return@filter p.buildFile.readText().contains(FRYBITS_LIBRARY_REGEX)
    }.forEach { p ->
        dependencies {
            constraints {
                add(JavaPlatformPlugin.API_CONFIGURATION_NAME, p)
            }
        }
    }
}
