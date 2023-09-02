import java.io.FileOutputStream

val moduleDirectory = layout.projectDirectory.dir("asm/modules")
val testsDirectory = layout.projectDirectory.dir("asm/tests")
val assemblyDirectory = layout.buildDirectory.dir("src")
val logDirectory = layout.buildDirectory.dir("log").get()
val assembly = "*.asm"

mkdir(logDirectory)

tasks.register<Copy>("prepareTests") {
    from(testsDirectory)
    include(assembly)
    into(assemblyDirectory)
}

tasks.register<Copy>("prepareModules") {
    from(moduleDirectory)
    include(assembly)
    into(assemblyDirectory)
    expand(AvrConstants().map())
}

testsDirectory.getAsFile().listFiles().forEach {
    tasks.register<Exec>(it.name.replace(Regex("[.-]"), "_")) {
        dependsOn(tasks.withType<Copy>())
        workingDir(assemblyDirectory)
        standardOutput = FileOutputStream(
            logDirectory.file(it.name.replace(".asm", ".log")).asFile
        )
        commandLine(
            "/opt/gavrasm/gavrasm",
            "-E", // Longer error comments
            "-S", // Symbol list in listing file
            "-M", // Expand macro code
            it.name
        )
    }
}

tasks.register<DefaultTask>("build") {
    dependsOn(tasks.withType<Exec>())
}
