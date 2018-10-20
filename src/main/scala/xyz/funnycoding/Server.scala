package xyz.funnycoding

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import xyz.funnycoding.config.Config
import xyz.funnycoding.service.HttpToElasticService

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] with Http4sDsl[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    import xyz.funnycoding.repository.EsRepository
    for {
      config <- Stream.eval(Config.load())
      exitCode <- BlazeBuilder[IO]
        .bindHttp(config.server.port, config.server.host)
        .mountService(new HttpToElasticService(new EsRepository).service, "/")
        .serve
    } yield exitCode
  }
}
