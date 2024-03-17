val sourceDirectory = layout.projectDirectory.dir("asm")
val moduleDirectory = sourceDirectory.dir("modules")
val testsDirectory = sourceDirectory.dir("tests")
val mainDirectory = sourceDirectory.dir("main")

val assembly = "*.asm"

// GAVRASM builds in the same directory as the source
// So we do this little dance to copy the source into the build directory

tasks.register<AvrConstants>("prepareConstants") {
    directory(layout.buildDirectory.get())
    file("constants.asm")
}

tasks.register<Copy>("prepareTests") {
    dependsOn("prepareConstants")
    from(testsDirectory)
    include(assembly)
    into(layout.buildDirectory)
}

tasks.register<Copy>("prepareMain") {
    dependsOn("prepareConstants")
    from(mainDirectory)
    include(assembly)
    into(layout.buildDirectory)
}

tasks.register<Copy>("prepareModules") {
    dependsOn("prepareConstants")
    from(moduleDirectory)
    include(assembly)
    into(layout.buildDirectory)
}

testsDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

mainDirectory.getAsFile().listFiles().forEach {
    addAssemblyTask(it)
}

fun addAssemblyTask(file: File) {
    val assemblyFile = AssemblyFile(file, layout.buildDirectory)
    tasks.register<Exec>(assemblyFile.taskName()) {
        dependsOn(tasks.withType<Copy>())
        workingDir(layout.buildDirectory)
        isIgnoreExitValue = true
        // The standardOutput isn't of much interest (the good stuff is in
        // ${name}.lst and ${name}.err) but it's here just in case we DO need
        // it.
        standardOutput = assemblyFile.log()
        executable("/opt/gavrasm/gavrasm")
        args(assemblyFile.args(listOf("longerErrors", "symbolList", "expandMacros")))
        doLast {
            if (executionResult.get().exitValue != 0) {
                throw GradleException(assemblyFile.errorText())
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
