package de.microtema.plugins.jenkinsfile

class JenkinsFileUtil {

    static def maskEnvironmentVariable(String value) {

        return "'" + (value ?: "") + "'"
    }

    static def paddingLine(String template, int padding) {

        def stringBuilder = new StringBuilder()
        def spaces = new ArrayList<>()

        while (padding-- > 0) {
            spaces.add(" ")
        }

        def paddingString = String.join("", spaces)

        def reader = new BufferedReader(new StringReader(template))

        int count = 0
        for (String line : reader.readLines()) {
            stringBuilder.append(count ? "\n" : "").append(paddingString).append(line)
            count++
        }
        reader.close()

        return stringBuilder.toString()
    }
}
