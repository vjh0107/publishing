package kr.junhyung.publishing

import org.gradle.api.Project

data class ProjectVersion(val value: String) {

    companion object {
        private const val SNAPSHOT_SUFFIX: String = "-SNAPSHOT"

        fun from(project: Project): ProjectVersion {
            return ProjectVersion(project.version.toString())
        }
    }

    fun isSnapshotVersion(): Boolean {
        return value.endsWith(SNAPSHOT_SUFFIX)
    }

}