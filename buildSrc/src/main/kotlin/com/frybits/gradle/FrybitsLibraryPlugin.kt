package com.frybits.gradle

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.gradle.GradleExternalDocumentationLinkBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import java.net.URI

/*
 *  Copyright 2014 Prateek Srivastava
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 */

class FrybitsLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyLibraryPlugins()

        target.configure<LibraryExtension> {
            configureAndroidLibrary()
        }

        target.configureDokka()
        target.apply<MavenPublishPlugin>()
    }
}

private fun Project.applyLibraryPlugins() {
    apply<LibraryPlugin>()
    apply<KotlinAndroidPluginWrapper>()

    configureCommon()
}

private fun LibraryExtension.configureAndroidLibrary() {
    configureCommonAndroid()

    buildTypes {
        maybeCreate("release").apply {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

private fun Project.configureDokka() {
    apply<DokkaPlugin>()
    val mmTask = tasks.findByName("dokkaHtmlMultiModule") as? DokkaMultiModuleTask
    if (mmTask != null) {
        mmTask.enabled = false
    }
    tasks.withType<DokkaTaskPartial> {
        dokkaSourceSets.configureEach {
            sourceLink {
                localDirectory.set(this@configureDokka.projectDir.resolve("src").resolve("main").resolve("kotlin"))
                remoteUrl.set(URI("https://github.com/pablobaxter/rx-preferences/tree/master/${this@configureDokka.name}/src/main/kotlin/").toURL())
                remoteLineSuffix.set("#L")
            }
            externalDocumentationLinks.add(
                GradleExternalDocumentationLinkBuilder(this@configureDokka).apply {
                    url.set(URI("https://reactivex.io/RxJava/2.x/javadoc/").toURL())
                }
            )
            externalDocumentationLinks.add(
                GradleExternalDocumentationLinkBuilder(this@configureDokka).apply {
                    url.set(URI("https://reactivex.io/RxJava/3.x/javadoc/").toURL())
                }
            )
            externalDocumentationLinks.add(
                GradleExternalDocumentationLinkBuilder(this@configureDokka).apply {
                    url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
                }
            )
//            val guavaVersion = this@configureDokka.the<VersionCatalogsExtension>()
//                .named("libs")
//                .findVersion("android-guava")
//                .get()
            externalDocumentationLinks.add(
                GradleExternalDocumentationLinkBuilder(this@configureDokka).apply {
//                    url.set(URI("https://google.github.io/guava/releases/$guavaVersion/api/docs/").toURL())
                    // Workaround for https://github.com/google/guava/issues/5653
                    url.set(URI("https://guava.dev/releases/29.0-android/api/docs/").toURL())
                }
            )
        }
    }
}
