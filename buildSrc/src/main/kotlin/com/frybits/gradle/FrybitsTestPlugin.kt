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

import com.android.build.api.dsl.TestExtension
import com.android.build.gradle.TestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class FrybitsTestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyTestPlugins()
        target.configure<TestExtension> {
            configureAndroidTest()
        }
    }
}

private fun Project.applyTestPlugins() {
    apply<TestPlugin>()
    apply<KotlinAndroidPluginWrapper>()

    configureCommon()
}

private fun TestExtension.configureAndroidTest() {
    configureCommonAndroid()
}
