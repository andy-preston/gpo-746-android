import java.io.FileOutputStream

val moduleDirectory = layout.projectDirectory.dir("asm/modules")
val testsDirectory = layout.projectDirectory.dir("asm/tests")
val assemblyDirectory = layout.buildDirectory.dir("src")
val assembly = "*.asm"

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
    val sourceName = it.name
    tasks.register<Exec>(sourceName.replace(".", "_").replace("-", "_")) {
        dependsOn(tasks.withType<Copy>())
        workingDir(assemblyDirectory)
        doFirst {
            standardOutput = FileOutputStream(
                // This isn't very nice - but I'm having trouble grasping
                // layout.buildDirectory works!
                "avr/build/src/" + sourceName.replace(".asm", ".log")
            )
        }
        commandLine(
            "/opt/gavrasm/gavrasm",
            "-E", // Longer error comments
            "-S", // Symbol list in listing file
            "-M", // Expand macro code
            sourceName
        )
    }
}

tasks.register<DefaultTask>("build") {
    dependsOn(tasks.withType<Exec>())
}
