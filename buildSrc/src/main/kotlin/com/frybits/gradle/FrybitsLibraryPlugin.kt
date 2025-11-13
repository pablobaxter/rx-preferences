/*
 *  Copyright 2022 Pablo Baxter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 * https://github.com/pablobaxter/rx-preferences
 */

package com.frybits.gradle

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

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
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

private fun Project.configureDokka() {
    apply<DokkaPlugin>()

    extensions.configure<DokkaExtension> {
        dokkaPublications.named("html") {
            suppressInheritedMembers.set(true)
            failOnWarning.set(true)
        }

        dokkaSourceSets.named("main") {
            sourceLink {
                localDirectory.set(layout.projectDirectory.dir("src").dir("main").dir("kotlin"))
                remoteUrl("https://github.com/pablobaxter/rx-preferences/tree/master/${this@configureDokka.name}/src/main/kotlin/")
                remoteLineSuffix.set("#L")
            }
            externalDocumentationLinks.create("rxJava2") {
                url("https://reactivex.io/RxJava/2.x/javadoc/")
            }
            externalDocumentationLinks.create("rxJava3") {
                url("https://reactivex.io/RxJava/3.x/javadoc/")
            }
            externalDocumentationLinks.create("coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
            }

        }
    }
}
