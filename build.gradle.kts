plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "kr.junhyung"
version = "1.0.0"
description = "A Gradle plugin for publishing artifacts to my own Nexus repository."

repositories {
    maven("https://junhyung.nexus/")
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    website = "https://github.com/vjh0107/publishing"
    vcsUrl = "https://github.com/vjh0107/publishing.git"
    plugins {
        register("publishing") {
            id = "kr.junhyung.publishing"
            displayName = "publishing"
            description = project.description
            implementationClass = "kr.junhyung.publishing.PublishingPlugin"
        }
    }
}

publishing {
    repositories {
        val url = if (project.version.toString().endsWith("-SNAPSHOT")) {
            "https://nexus.junhyung.kr/repository/maven-snapshots/"
        } else {
            "https://nexus.junhyung.kr/repository/maven-releases/"
        }
        maven(url) {
            credentials {
                username = System.getenv("NEXUS_USERNAME").toString()
                password = System.getenv("NEXUS_PASSWORD").toString()
            }
        }
    }
}