package app

import zio.{ExitCode, URIO, ZIO, ZIOAppDefault}
import service.{FbBotZio, FbBotZioImpl}

object Main extends ZIOAppDefault{

  val botEffect: ZIO[FbBotZio,Throwable,Unit] =
    for {
      bot <- ZIO.service[FbBotZio]
      _ <- bot.startBot.catchAll{
        case ex: Throwable => ZIO.logError(s"Exception ${ex.getMessage} - ${ex.getCause}")
      }
    } yield ()

  val MainApp: ZIO[Any, Throwable, Unit] = for {
    _ <- botEffect.provide(FbBotZioImpl.layer)
  } yield ()

  override def run: URIO[Any,ExitCode] = MainApp.exitCode

}
