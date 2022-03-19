package kindleclock.domain.eff

import kindleclock.domain.model.error.KindleClockError
import org.atnos.eff./=

object KindleClockEitherEffect extends KindleClockEitherEffect

trait KindleClockEitherEffect extends KindleClockEitherTypes {}

trait KindleClockEitherTypes {
  type KindleClockEither[A] = Either[KindleClockError, A]

  type _kindleClockEither[R] = KindleClockEither /= R
}
