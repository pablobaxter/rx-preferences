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
