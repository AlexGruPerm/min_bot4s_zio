package service

import zhttp.service.server.ServerSSLHandler.ServerSSLOptions
import zhttp.http._
import zhttp.service.Server
import zhttp.service.server.ServerChannelFactory
import zhttp.service.EventLoopGroup
import zhttp.http.{Http, Method, Request, Response}
import zio.{Ref, Scope, ZIO}

//todo: maybe     new DefaultAsyncHttpClientConfig.sslContext
//todo: https://asynchttpclient.github.io/async-http-client/ssl.html

class telegramBotZio(sslContext: io.netty.handler.ssl.SslContext, private val started: Ref.Synchronized[Boolean])
{
  private def callback: Http[Any,Throwable,Request,Response] = Http.collectZIO[Request]{
    case req @ Method.POST -> !! =>
      for {
        _ <- ZIO.logInfo("call callback")
        body    <- req.bodyAsString
        _ <- ZIO.logInfo(s"body = [$body]")
        //update  <- ZIO.attempt(fromJson[Update](body))
        //_ <- receiveUpdate(update, None)
      } yield Response.ok
  }

  val sslOptions: ServerSSLOptions = ServerSSLOptions(sslContext)

  private def server: Server[Any,Throwable] = Server.port(8443) ++ Server.app(callback) ++ Server.ssl(sslOptions)

  def run: ZIO[Any,Throwable,Nothing] = {
    server.make
      .flatMap(start => ZIO.logInfo(s"Server started on ${start.port} ") *> ZIO.never)
      .provide(ServerChannelFactory.auto, EventLoopGroup.auto(1), Scope.default)
    /* *>
    started.updateZIO { isStarted =>
      for {
        _ <- ZIO.when(isStarted)(ZIO.fail(new Exception("Bot already started")))
        wh = SetWebhook(url = webhookUrl, certificate = certificate, allowedUpdates = allowedUpdates)
        reqSetWebhook = request(wh)
        response <- reqSetWebhook.catchAll{
          case ex: Throwable => ZIO.logError(s"SetWebhook exception ${ex.getMessage} - ${ex.getCause}").orDie
        } *> ZIO.succeed(true)
      } yield response
    }
    */
  }

}