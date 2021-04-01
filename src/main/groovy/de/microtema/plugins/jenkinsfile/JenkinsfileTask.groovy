package de.microtema.plugins.jenkinsfile

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class JenkinsfileTask extends DefaultTask {

    @Input
    final Property<String> serviceName = project.objects.property(String)

    @Input
    final ListProperty<String> upstreamProjects = project.objects.listProperty(String)

    @Input
    final ListProperty<String> prodStages = project.objects.listProperty(String)

    @Input
    final MapProperty<String, String> stages = project.objects.mapProperty(String, String)

    @Input
    final MapProperty<String, String> environments = project.objects.mapProperty(String, String)

    @OutputFile
    def resultFile = new File('./Jenkinsfile')

    @Internal
    final def service = new JenkinsfileGeneratorService(project)

    @TaskAction
    def generate() {

        // Skip maven sub modules
        if (!service.isGitRepo() && false) {

            logger.info "Skip maven module: ${serviceName} since it is not a git repo!"

            return
        }

        def rootPath = service.getRootPath()

        logger.info "Generate Jenkinsfile for ${serviceName} -> ${rootPath}"

        def pipeline = getJenkinsStage 'pipeline'
        def agent = getJenkinsStage 'agent'
        def environment = getJenkinsStage 'environment'
        def options = getJenkinsStage 'options'
        def triggers = getJenkinsStage 'triggers'
        def stages = buildStages()

        pipeline = pipeline.replace("@AGENT@", agent)
                .replace("@ENVIRONMENT@", environment)
                .replace("@OPTIONS@", options)
                .replace("@TRIGGERS@", triggers)
                .replace("@STAGES@", stages)

        resultFile.write pipeline

        logger.info "File written to $resultFile"
    }

    def getJenkinsStage(String templateName) {

        def inputStream = getClass().getResourceAsStream("/${templateName}.Jenkinsfile")

        if (!inputStream) {
            logger.info "Unable to find template $templateName"
        }

        def template = inputStream.getText()

        switch (templateName) {
            case 'environment': return applyEnvironmentStage(template)
            case 'triggers': return applyTriggersStage(template)
            case 'tests': return applyTestsStage(template)
            case 'sonar': return applySonarStage(template)
            case 'docker': return applyDockerStage(template)
            default: return template
        }
    }

    def buildStages() {

        def stageNames = Arrays.asList("compile", "versioning", "tests", "build", "sonar", "docker")

        def template = new StringBuilder()

        for (String stageName : stageNames) {

            def stageTemplate = getJenkinsStage stageName

            if (stageTemplate) {
                template.append("\n")
                template.append(paddLine(stageTemplate, 8))
            }
        }

        template.toString()
    }

    def applyEnvironmentStage(String template) {

        def environmentsAsString = new StringBuilder()

        def environments = new HashMap<>(this.environments.get())
        def serviceName = this.serviceName.get()

        environments.putIfAbsent("SERVICE_NAME", serviceName)

        for (Map.Entry<String, String> entry : environments.entrySet()) {

            String value = entry.getValue()

            if (!(value.startsWith("sh") || value.startsWith("'") || value.startsWith("\""))) {
                value = maskEnvironmentVariable(value)
            }

            String line = "${entry.getKey()} = ${value}"
            environmentsAsString.append(paddLine(line, 8))
        }

        return template.replace("@ENVIRONMENTS@", environmentsAsString.toString())
    }

    def applyTriggersStage(String template) {

        def upstreamProjectsParam = new StringBuilder()
        def upstreamProjects = this.upstreamProjects.get()

        for (int index = 0; index < upstreamProjects.size(); index++) {

            String upstreamProject = upstreamProjects.get(index)

            upstreamProjectsParam.append(upstreamProject)
            upstreamProjectsParam.append('''/${env.BRANCH_NAME.replaceAll('/', '%2F')}''')

            if (index < upstreamProjects.size() - 1) {
                upstreamProjectsParam.append(",")
            }
        }

        return template.replace("@UPSTREAM_PROJECTS@", upstreamProjectsParam.toString())
    }

    def applyTestsStage(String template) {

        def existsUnitTests = service.existsUnitTests()
        def existsIntegrationTests = service.existsIntegrationTests()

        if (!existsUnitTests && !existsIntegrationTests) {
            return null
        }

        def unitTest = existsUnitTests ? getJenkinsStage('unit-test') : ""
        def integrationTest = existsIntegrationTests ? getJenkinsStage('integration-test') : ""

        return template
                .replace("@UNIT_TESTS@", unitTest.toString())
                .replace("@INTEGRATION_TESTS@", integrationTest.toString())
    }

    def applySonarStage(String template) {

        if (!service.hasSonarTask()) {
            return null
        }

        template
    }

    def applyDockerStage(String template) {

        if (!service.existsDockerfile()) {
            return null
        }

        template
    }

    def maskEnvironmentVariable(String value) {

        return "'" + (value ?: "") + "'"
    }

    def paddLine(String template, int padding) {

        def reader = new BufferedReader(new StringReader(template))
        def builder = new StringBuilder()

        List<String> spaces = new ArrayList<>()
        while (padding-- > 0) {
            spaces.add(" ")
        }

        String paddingString = String.join("", spaces)

        try {
            String line
            while ((line = reader.readLine()) != null) {
                builder.append(paddingString).append(line).append("\n")
            }

        } catch (IOException e) {
            throw new IllegalStateException(e)
        } finally {
            try {
                reader.close()
            } catch (IOException e) {
                // ignore this exception
            }
        }

        return builder.toString()
    }
}
