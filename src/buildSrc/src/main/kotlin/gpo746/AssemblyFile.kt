import org.gradle.api.file.DirectoryProperty
import java.io.File
import java.io.FileOutputStream

final class AssemblyFile(file: File, buildDirectory: DirectoryProperty) {

    private val fullName = file.name

    private val shortName = fullName.substring(0, fullName.lastIndexOf('.'))

    private val directory = buildDirectory

    private val optionMap = mapOf(
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

    public fun taskName() = shortName.replace(Regex("[.-]"), "_")

    private fun buildFile(extension: String): File = directory.file(
        "$shortName.$extension"
    ).get().asFile

    public fun log() = FileOutputStream(buildFile("log"))

    public fun errorText() = buildFile("err").readText()

    public fun args(longOptions: List<String>): List<String> {
        val shortArgs = longOptions.map({
            optionMap[it]
        })
        return shortArgs.filterNotNull() + fullName
    }
}
