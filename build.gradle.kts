/*
 * MIT License
 *
 * Copyright (c) 2023.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
    `java-library`
    signing
}

group = "com.yunkuangao"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlin", "kotlin-stdlib", "1.7.20")

    // test
    testImplementation("org.jetbrains.kotlin", "kotlin-test", "1.7.20")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

// publish to public repository
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "tool"
            version = System.getenv("VERSION")
            from(components["java"])
        }
    }
    repositories {
        // Maven Central
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("ORG_GRADLE_PROJECT_sonatypeUsername")
                password = System.getenv("ORG_GRADLE_PROJECT_sonatypePassword")
            }
        }
        // GitHub Release
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/yunkuangao/timeline-data")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}