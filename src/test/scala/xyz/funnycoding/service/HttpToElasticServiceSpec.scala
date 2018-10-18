package xyz.funnycoding.service

import cats.effect.IO
import io.circe.Json
import io.circe.literal._
import org.http4s.dsl.io.{GET, _}
import org.http4s.{Request, Response, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import org.http4s.circe._
import org.http4s.dsl.io._

class HttpToElasticServiceSpec extends WordSpec with MockFactory with Matchers {

  private val service = new HttpToElasticService().service

  "httpToElasticService" should {
    "validate request" in {
      val index = "hello"
      val eventId = "1"
      val response = serve(Request[IO](GET, Uri.unsafeFromString(s"/es/$index/$eventId")))

      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe json"""
        {
          "index": $index,
          "eventId": $eventId
        }"""
    }

    "invalidate request" in {
      val index = "hello"
      val eventId = ""
      val response = serve(Request[IO](GET, Uri.unsafeFromString(s"/es/$index/$eventId")))

      response.status shouldBe Status.BadRequest
      response.as[String].unsafeRunSync() shouldBe "stop bullshit man"
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    service.orNotFound(request).unsafeRunSync()
  }


}
