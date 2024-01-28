import java.io.FileOutputStream
import org.apache.tools.ant.filters.ReplaceTokens

val moduleDirectory = layout.projectDirectory.dir("asm/modules")
val testsDirectory = layout.projectDirectory.dir("asm/tests")
val mainDirectory = layout.projectDirectory.dir("asm/main")

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
    addAssemblyTask(it.name)
}

mainDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it.name)
}

fun addAssemblyTask(name: String) {
    tasks.register<Exec>(name.replace(Regex("[.-]"), "_")) {
        dependsOn(tasks.withType<Copy>())
        workingDir(layout.buildDirectory)
        standardOutput = FileOutputStream(
            layout.buildDirectory.get().file(
                name.replace(".asm", ".log")
            ).asFile
        )
        commandLine(
            "/opt/gavrasm/gavrasm",
            "-E", // Longer error comments
            "-S", // Symbol list in listing file
            "-M", // Expand macro code
            name
        )
    }
}

tasks.register<DefaultTask>("build") {
    dependsOn(tasks.withType<Exec>())
}
