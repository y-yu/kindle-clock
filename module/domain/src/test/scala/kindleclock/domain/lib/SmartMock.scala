package kindleclock.domain.lib

import org.mockito.Mockito
import scala.reflect.ClassTag

trait SmartMock {

  protected[this] def smartMock[T: ClassTag]: T =
    Mockito.mock(implicitly[ClassTag[T]].runtimeClass, Mockito.RETURNS_SMART_NULLS).asInstanceOf[T]

}
