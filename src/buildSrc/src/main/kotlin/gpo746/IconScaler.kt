import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal final class IconScaler(sourceName: String) {

    private val sourceImage: BufferedImage = ImageIO.read(File(sourceName))

    public fun resizeImage(
        destinationName: String,
        scaleWidth: Int,
        scaleHeight: Int
    ) {
        val scaledImage = sourceImage.getScaledInstance(
            scaleWidth,
            scaleHeight,
            Image.SCALE_SMOOTH
        )
        val destinationImage = BufferedImage(
            scaleWidth,
            scaleHeight,
            BufferedImage.TYPE_INT_RGB
        )
        destinationImage.getGraphics().drawImage(scaledImage, 0, 0, null)
        // make sure that outputFile exists.
        // If it doesn't, write() will throw a NullPointerException
        val destinationFile = File(destinationName)
        ImageIO.write(destinationImage, "jpg", destinationFile)
    }
}
