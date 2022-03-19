package kindleclock.domain.model.error

abstract class KindleClockError(
  message: String = null,
  cause: Throwable = null
) extends Throwable(message, cause)
