package kindleclock.primary.util.image

import java.awt.image.BufferedImage
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.SVGAbstractTranscoder

class GrayscalePNGTranscoder extends PNGTranscoder {
  hints.put(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, true)

  override def createImage(width: Int, height: Int) =
    new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
}
