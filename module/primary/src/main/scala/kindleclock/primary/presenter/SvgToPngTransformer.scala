package kindleclock.primary.presenter

import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.StringReader
import kindleclock.domain.model.KindleClockColor
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import scala.xml.Elem

class SvgToPngTransformer {
  def transform(
    svg: Elem,
    backgroundColor: KindleClockColor
  ): Array[Byte] = {
    val svgReader = new StringReader(svg.toString())
    val doc =
      new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName)
        .createSVGDocument(
          null, // Is it actually OK?
          svgReader
        )

    val transcoderInput = new TranscoderInput(doc)
    val pngStream = new ByteArrayOutputStream
    val output = new TranscoderOutput(pngStream)

    val t = new GrayscalePNGTranscoder
    t.addTranscodingHint(
      ImageTranscoder.KEY_BACKGROUND_COLOR,
      backgroundColor match {
        case KindleClockColor.Black =>
          Color.BLACK
        case KindleClockColor.White =>
          Color.WHITE
      }
    )
    t.transcode(transcoderInput, output)

    pngStream.toByteArray
  }
}
