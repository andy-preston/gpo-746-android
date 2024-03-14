import java.io.FileOutputStream

val sourceDirectory = layout.projectDirectory.dir("asm")
val moduleDirectory = sourceDirectory.dir("modules")
val testsDirectory = sourceDirectory.dir("tests")
val mainDirectory = sourceDirectory.dir("main")

val assembly = "*.asm"
val gavrasmOptions = mapOf(
    "noList" to "-L",
    "expandMacros" to "-M",
    "quiet" to "-Q",
    "symbolList" to "-S",
    "beginnersErrors" to "-B",
    "ansiOutput" to "-A",
    "longerErrors" to "-E",
    "listDirectives" to "-D",
    "enableWrap" to "-W",
    "supportedAvrTypes" to "-T",
    "internalDefIncludeOff" to "-X",
    "defineDateConstants" to "-Z"
)

// GAVRASM builds in the same directory as the source
// So we do this little dance to copy the source into the build directory

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
}

tasks.register("prepareConstants") {
    doLast {
        AvrConstants().fileOutput(
            layout.buildDirectory.file("constants.asm").get().asFile
        )
    }
}

testsDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

mainDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

fun buildFile(name: String): File = layout.buildDirectory.file(name).get().asFile

fun addAssemblyTask(file: File) {
    val fullName = file.name
    val name = fullName.substring(0, fullName.lastIndexOf('.'))
    tasks.register<Exec>(name.replace(Regex("[.-]"), "_")) {
        dependsOn(tasks.withType<Copy>(), "prepareConstants")
        workingDir(layout.buildDirectory)
        isIgnoreExitValue = true
        // The standardOutput isn't of much interest (the good stuff is in
        // ${name}.lst and ${name}.err) but it's here just in case we DO need
        // it.
        standardOutput = FileOutputStream(buildFile("$name.log"))
        commandLine(
            "/opt/gavrasm/gavrasm",
            gavrasmOptions["longerErrors"],
            gavrasmOptions["symbolList"],
            gavrasmOptions["expandMacros"],
            fullName
        )
        doLast {
            if (executionResult.get().exitValue != 0) {
                throw GradleException(buildFile("$name.err").readText())
            }
        }
    }
}

tasks.register<DefaultTask>("avr") {
    dependsOn(tasks.withType<Exec>())
}

tasks.register<DefaultTask>("build") {
    dependsOn("avr")
}
