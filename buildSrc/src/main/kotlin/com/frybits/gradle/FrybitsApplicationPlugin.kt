package com.frybits.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.internal.ParcelizeSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

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

class FrybitsApplicationPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.applyAppPlugins()

        target.configure<ApplicationExtension> {
            configureAndroidApplication()
        }
    }
}

private fun Project.applyAppPlugins() {
    apply<AppPlugin>()
    apply<KotlinAndroidPluginWrapper>()
    apply<ParcelizeSubplugin>()
    configureCommon()
}

private fun ApplicationExtension.configureAndroidApplication() {
    configureCommonAndroid()

    buildTypes {
        maybeCreate("release").apply {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures.viewBinding = true

    packaging {
        resources {
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}
