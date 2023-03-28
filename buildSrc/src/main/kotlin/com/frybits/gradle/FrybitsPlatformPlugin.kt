package com.frybits.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

private val FRYBITS_LIBRARY_REGEX = "^plugins \\{(?s).*\\n\\s+id ('frybits-library'|\"frybits-library\")(?s).*}\$".toRegex()

class FrybitsPlatformPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlatformPlugin()
        target.applyPublishingPlugin()
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
                add(JavaPlatformPlugin.API_CONFIGURATION_NAME, p.path)
            }
        }
    }
}

private fun Project.applyPublishingPlugin() {
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()
    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                whenObjectAdded {
                    if (this is MavenPublication) {
                        configurePom(
                            findProperty("libraryName")?.toString().orEmpty(),
                            findProperty("description")?.toString().orEmpty()
                        )
                    }
                }
                create<MavenPublication>("release") {
                    from(components["javaPlatform"])
                }
            }
        }
    }

    configure<SigningExtension> {
        sign(extensions.getByType<PublishingExtension>().publications)
    }
}

private fun MavenPublication.configurePom(
    projectName: String,
    projectDescription: String
) {
    pom {
        name.set(projectName)
        description.set(projectDescription)
        url.set("https://github.com/pablobaxter/rx-preferences")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                id.set("pablobaxter")
                name.set("Pablo Baxter")
                email.set("pablo@frybits.com")
            }
            developer {
                id.set("f2prateek")
                name.set("Prateek Srivastava")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/pablobaxter/rx-preferences.git")
            developerConnection.set("git:ssh://github.com/pablobaxter/rx-preferences.git")
            url.set("https://github.com/pablobaxter/rx-preferences")
        }
    }
}
