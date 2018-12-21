/*
 *    Copyright 2017 Trevor Jones
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

plugins {
  kotlin("jvm")
  `maven-publish`
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "0.10.0"
}

pluginBundle {
  website = "https://github.com/trevjonez/composer-gradle-plugin"
  vcsUrl = "git@github.com:trevjonez/composer-gradle-plugin.git"
  tags = listOf("android", "composer", "test", "orchestrator", "report")
}

val classpathManifest = tasks.register("createClasspathManifest") {
  val outputDir = file("$buildDir/$name")

  val runtimeClasspath = sourceSets.getByName("main").runtimeClasspath
  inputs.files(runtimeClasspath)
  outputs.dir(outputDir)

  doLast {
    outputDir.mkdirs()
    file("$outputDir/classpath-manifest.txt")
        .writeText(runtimeClasspath.joinToString(separator = "\n"))
  }
}

dependencies {
  compile(gradleApi())

  testCompile(gradleTestKit())
  testCompile("junit:junit:4.12")
  testCompile("org.assertj:assertj-core:3.5.2")
  testCompile("commons-io:commons-io:2.5")

  testRuntime(files(classpathManifest))
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
  classifier = "sources"
  from(sourceSets["main"].allSource)
  dependsOn(sourceSets["main"].classesTaskName)
}

publishing {
  publications {
    register<MavenPublication>("core") {
      from(project.components.getByName("java"))
      artifact(sourcesJar.get())
      pom {
        inceptionYear.set("2017")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
          }
        }
      }
    }
  }
}