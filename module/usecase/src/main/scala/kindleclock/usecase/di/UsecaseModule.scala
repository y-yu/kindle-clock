package kindleclock.usecase.di

import com.google.inject.AbstractModule
import kindleclock.domain.interface.usecase.GetKindleClockInfoUsecase
import kindleclock.usecase.GetKindleClockInfoUsecaseImpl

class UsecaseModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GetKindleClockInfoUsecase]).to(classOf[GetKindleClockInfoUsecaseImpl])
  }
}
