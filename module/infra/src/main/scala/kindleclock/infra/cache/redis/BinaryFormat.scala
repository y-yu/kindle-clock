package kindleclock.infra.cache.redis

import scalapb.GeneratedMessage
import scalapb.GeneratedMessageCompanion
import scala.util.Try

trait BinaryFormat[A] {
  def parseFrom(binary: Array[Byte]): Try[A]

  def toBytes(a: A): Array[Byte]
}

object BinaryFormat {
  implicit def instanceFromGeneratedMessageCompanion[A <: GeneratedMessage](implicit
    gmc: GeneratedMessageCompanion[A]
  ): BinaryFormat[A] =
    new BinaryFormat[A] {
      override def parseFrom(binary: Array[Byte]): Try[A] =
        Try(gmc.parseFrom(binary))

      override def toBytes(a: A): Array[Byte] =
        gmc.toByteArray(a)
    }
}
