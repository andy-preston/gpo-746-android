import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ConstantsSourceFile : DefaultTask() {

    private lateinit var theDirectory: Directory
    private lateinit var theFile: String

    public fun directory(dir: Directory) {
        theDirectory = dir
    }

    public fun file(f: String) {
        theFile = f
    }

    protected abstract fun writeFile(constFile: File)

    @TaskAction
    fun taskAction() {
        writeFile(theDirectory.file(theFile).asFile)
    }
}
