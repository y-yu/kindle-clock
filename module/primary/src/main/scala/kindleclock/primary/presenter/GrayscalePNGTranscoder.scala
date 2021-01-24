package kindleclock.primary.presenter

import java.awt.image.BufferedImage
import org.apache.batik.transcoder.image.PNGTranscoder

class GrayscalePNGTranscoder extends PNGTranscoder {
  override def createImage(width: Int, height: Int) =
    new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
}
