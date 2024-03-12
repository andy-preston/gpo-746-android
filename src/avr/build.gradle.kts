import java.io.FileOutputStream
import org.apache.tools.ant.filters.ReplaceTokens

val sourceDirectory = layout.projectDirectory.dir("asm");
val moduleDirectory = sourceDirectory.dir("modules")
val testsDirectory = sourceDirectory.dir("tests")
val mainDirectory = sourceDirectory.dir("main")

val assembly = "*.asm"

tasks.register<Copy>("prepareTests") {
    from(testsDirectory)
    include(assembly)
    into(layout.buildDirectory)
}

tasks.register<Copy>("prepareMain") {
    from(mainDirectory)
    include(assembly)
    into(layout.buildDirectory)
}

tasks.register<Copy>("prepareModules") {
    from(moduleDirectory)
    include(assembly)
    into(layout.buildDirectory)
    filter(
        ReplaceTokens::class,
        "tokens" to AvrConstants().map()
    )
}

testsDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

mainDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

fun addAssemblyTask(file: File) {
    val fullName = file.name
    val name = fullName.substring(0, fullName.lastIndexOf('.'))
    tasks.register<Exec>(name.replace(Regex("[.-]"), "_")) {
        dependsOn(tasks.withType<Copy>())
        workingDir(layout.buildDirectory)
        isIgnoreExitValue = true
        standardOutput = FileOutputStream(
            layout.buildDirectory.file("${name}.log").get().asFile
        )
        commandLine("/opt/gavrasm/gavrasm", "-E", "-S", "-M", name)
        doLast {
            if (executionResult.get().exitValue != 0) {
                val errors = layout.buildDirectory.file(
                    "${name}.err"
                ).get().asFile.readLines().joinToString(
                    separator="\n"
                )
                throw GradleException(errors)
            }
        }
    }
}

tasks.register<DefaultTask>("build") {
    dependsOn(tasks.withType<Exec>())
}
