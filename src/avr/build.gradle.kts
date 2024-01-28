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
    val simpleName = name.replace(Regex("[.-]"), "_")
    tasks.register<Exec>(simpleName) {
        dependsOn(tasks.withType<Copy>())
        workingDir(layout.buildDirectory)
        commandLine("/usr/local/bin/gavrasm", name)
    }
}

tasks.register<DefaultTask>("build") {
    dependsOn(tasks.withType<Exec>())
}
