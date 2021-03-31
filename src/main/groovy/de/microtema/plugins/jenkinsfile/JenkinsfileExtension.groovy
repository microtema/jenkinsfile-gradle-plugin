package de.microtema.plugins.jenkinsfile

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

import javax.inject.Inject

class JenkinsfileExtension {

    final Property<String> serviceName
    final MapProperty<String, String> environments
    final ListProperty<String> upstreamProjects

    @Inject
    JenkinsfileExtension(ObjectFactory objects) {
        serviceName = objects.property(String)
        environments = objects.mapProperty(String, String)
        upstreamProjects = objects.listProperty(String)
    }
}
