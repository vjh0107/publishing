package kr.junhyung.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.maven

class PublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin(MavenPublishPlugin::class)) {
            target.plugins.apply(MavenPublishPlugin::class)
        }
        val publishingExtension = target.extensions.getByType<PublishingExtension>()

        val plugins = target.plugins
        val softwareComponents = target.components

        createMavenPublication(publishingExtension, plugins, softwareComponents)

        val projectVersion = ProjectVersion.from(target)
        addMavenRepositories(publishingExtension, projectVersion)
    }

    private fun createMavenPublication(
        publishingExtension: PublishingExtension,
        plugins: PluginContainer,
        softwareComponents: SoftwareComponentContainer,
    ) {
        publishingExtension.publications {
            create<MavenPublication>("maven") {
                if (plugins.hasPlugin(JavaPlugin::class)) {
                    from(softwareComponents["java"])
                }

                if (plugins.hasPlugin(JavaPlatformPlugin::class)) {
                    from(softwareComponents["javaPlatform"])
                }
            }
        }
    }

    private fun addMavenRepositories(publishingExtension: PublishingExtension, projectVersion: ProjectVersion) {
        val username = System.getenv("NEXUS_USERNAME")
        val password = System.getenv("NEXUS_PASSWORD")
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return
        }

        publishingExtension.repositories {
            val url = getRepositoryUrl(projectVersion)
            maven(url) {
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }

    private fun getRepositoryUrl(projectVersion: ProjectVersion): String {
        return if (projectVersion.isSnapshotVersion()) {
            "https://nexus.junhyung.kr/repository/maven-snapshots/"
        } else {
            "https://nexus.junhyung.kr/repository/maven-releases/"
        }
    }

}