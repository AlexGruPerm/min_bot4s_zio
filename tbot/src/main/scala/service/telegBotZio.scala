package service

import zio.{Task, _}
import io.netty.handler.ssl.{SupportedCipherSuiteFilter}
import java.io.{FileInputStream, InputStream}
import java.security.{KeyStore}
import javax.net.ssl.{KeyManagerFactory, TrustManagerFactory}

//1. trait
abstract class FbBotZio {

  val password: Array[Char] = "333333".toCharArray

  val ks: KeyStore = KeyStore.getInstance("PKCS12")
  val keystore: InputStream = new FileInputStream("E:\\PROJECTS\\min_bot4s_zio\\tbot\\src\\main\\resources\\mts.p12")

  ks.load(keystore, password)

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  trustManagerFactory.init(ks)

  import io.netty.handler.ssl.SslContextBuilder

  val sslContext: io.netty.handler.ssl.SslContext = SslContextBuilder.
    forServer(keyManagerFactory)
    .ciphers(null, SupportedCipherSuiteFilter.INSTANCE)
    .trustManager(trustManagerFactory)
    .keyManager(keyManagerFactory)
    .protocols("TLSv1.3")
    .startTls(true)
    .sessionCacheSize(32L)
    .build()

  def startBot: Task[Unit] =
    for {
      _ <- ZIO.logInfo("Begin start bot")
      _ <- Ref.Synchronized.make(false).flatMap { started =>
        new telegramBotZio(sslContext, started).run
      }.catchAll {
        case ex: Throwable => ZIO.logError(s" Exception FbBotZio.runBot ${ex.getMessage} - ${ex.getCause} ")
      }
      _ <- ZIO.logInfo("End bot")
    } yield ()

}

//3. Service implementations (classes) should accept all dependencies in constructor
case class FbBotZioImpl(clock: Clock) extends FbBotZio {
  super.startBot
}

//4. converting service implementation into ZLayer
object FbBotZioImpl {
  val layer =
    ZLayer {
      for {
        clock <- ZIO.clock
      } yield FbBotZioImpl(clock)
    }
}