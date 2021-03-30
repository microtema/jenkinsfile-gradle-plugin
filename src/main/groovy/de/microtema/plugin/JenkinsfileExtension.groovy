package de.microtema.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class JenkinsfileExtension {

    final Property<String> appName

    @Inject
    JenkinsfileExtension(ObjectFactory objects) {
        appName = objects.property(String)
    }
}
