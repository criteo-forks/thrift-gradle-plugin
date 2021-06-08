/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jruyi.gradle.thrift.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class ThriftPlugin implements Plugin<Project> {

    public static final String COMPILE_THRIFT_TASK = 'compileThrift'
    public static final String CHECK_THRIFT_TASK = 'checkThrift'

    public static final String THRIFT_VERSION = '0.9'

    @Override
    void apply(Project project) {

        project.configurations {
            thriftExecutable {
                description = "Configuration used to get a thrift executable"
                canBeResolved = true
                canBeConsumed = false
                defaultDependencies { deps ->
                    deps.add(project.dependencies.create("com.criteo.devtools:thrift:$THRIFT_VERSION") {
                        artifact {
                            name = "thrift"
                            type = "tgz"
                        }
                    })
                }
            }
        }

        project.repositories {
            maven {
                url 'http://build-nexus.crto.in/repository/criteo.thirdparty'
                content {
                    // this repository *only* contains artifacts with group "com.criteo.devtools" module "thrift"
                    includeModule("com.criteo.devtools", "thrift")
                }
            }
        }

        def srcDir = "${project.projectDir}/src/main/thrift"
        def dstDir = "${project.buildDir}/generated-sources/thrift"

        CompileThrift compileThrift = project.tasks.create(COMPILE_THRIFT_TASK, CompileThrift)
        compileThrift.sourceDir(srcDir)
        compileThrift.outputDir(dstDir)

        def checkThriftTask = project.tasks.register(CHECK_THRIFT_TASK, Copy) {
            from project.configurations.thriftExecutable.elements.map { project.tarTree(it.first()) }
            into project.layout.buildDirectory.dir('thrift')
        }
        compileThrift.thriftBinaryFolder.set(project.layout.projectDirectory.dir(checkThriftTask.map { it.destinationDir.absolutePath }))
        compileThrift.dependsOn(checkThriftTask)

        project.configure(project) {
            sourceSets {
                main {
                    resources {
                        srcDirs srcDir
                        // If the project don't already has includes, then all the resources will be included.
                        if (!includes.isEmpty()) {
                            // If the project already has includes, then enrich the include rules to include thrift files
                            include '**/*.thrift'
                        }
                    }
                }
            }
        }
    }
}
