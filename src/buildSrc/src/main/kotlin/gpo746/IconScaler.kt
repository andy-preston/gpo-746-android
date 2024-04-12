import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskAction
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

open class IconScaler : DefaultTask() {

    private lateinit var resourceDirectory: Directory

    private lateinit var sourceUrl: String

    private var sourceImageCache: BufferedImage? = null

    private val sizes = mapOf(
        "mdpi" to 48,
        "hdpi" to 72,
        "xhdpi" to 96,
        "xxhdpi" to 144,
        "xxxhdpi" to 192
    )

    public fun directory(dir: Directory) {
        resourceDirectory = dir
    }

    public fun url(u: String) {
        sourceUrl = u
    }

    private fun destinationFile(directoryName: String): File {
        return resourceDirectory.dir(directoryName).file("ic_launcher.png").asFile
    }

    private fun sourceImage(): BufferedImage {
        if (sourceImageCache == null) {
            sourceImageCache = ImageIO.read(File(sourceUrl))
        }
        return sourceImageCache!!
    }

    @TaskAction
    public fun taskAction() {
        val files = resourceDirectory.asFile.list()
        for ((sizeName, size) in sizes) {
            val directoryName = "mipmap-$sizeName"
            if (!files.contains(directoryName)) {
                resizeImage(destinationFile(directoryName), size)
            }
        }
    }

    public fun resizeImage(destinationFile: File, scaleSize: Int) {
        val scaledImage = sourceImage().getScaledInstance(
            scaleSize,
            scaleSize,
            Image.SCALE_SMOOTH
        )
        val destinationImage = BufferedImage(
            scaleSize,
            scaleSize,
            BufferedImage.TYPE_INT_RGB
        )
        destinationImage.getGraphics().drawImage(scaledImage, 0, 0, null)
        ImageIO.write(destinationImage, "jpg", destinationFile)
    }
}
