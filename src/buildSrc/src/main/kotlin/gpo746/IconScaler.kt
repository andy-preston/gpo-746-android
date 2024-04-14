import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskAction
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

open class IconScaler : DefaultTask() {

    private lateinit var resourceDirectory: Directory

    private lateinit var sourceName: String

    public fun directory(dir: Directory) {
        resourceDirectory = dir
    }

    public fun source(src: String) {
        sourceName = src
    }

    private var sourceImage: BufferedImage? = null

    private val sizes = mapOf(
        "mdpi" to 48,
        "hdpi" to 72,
        "xhdpi" to 96,
        "xxhdpi" to 144,
        "xxxhdpi" to 192
    )

    @TaskAction
    public fun taskAction() {
        val files = resourceDirectory.asFile.list()
        for ((sizeName, size) in sizes) {
            val directoryName = "mipmap-$sizeName"
            if (!files.contains(directoryName)) {
                processImage(directoryName, size)
            }
        }
    }

    private fun processImage(directoryName: String, size: Int) {
        if (sourceImage == null) {
            sourceImage = ImageIO.read(File(sourceName))
        }
        ImageIO.write(
            if (alreadyRightSize(size)) sourceImage!! else scaled(size),
            "png",
            destinationFile(directoryName)
        )
    }

    private fun alreadyRightSize(size: Int): Boolean =
        sourceImage!!.getWidth() == size && sourceImage!!.getHeight() == size

    private fun scaled(size: Int): BufferedImage {
        val scaledImage = sourceImage!!.getScaledInstance(
            size,
            size,
            Image.SCALE_SMOOTH
        )
        val destination = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        destination.getGraphics().drawImage(scaledImage, 0, 0, null)
        return destination
    }

    private fun destinationFile(directoryName: String): File {
        val subDirectory = resourceDirectory.dir(directoryName)
        subDirectory.asFile.mkdirs();
        return subDirectory.file("ic_launcher.png").asFile
    }
}
