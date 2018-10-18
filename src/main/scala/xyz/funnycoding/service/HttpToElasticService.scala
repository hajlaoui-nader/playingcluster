package xyz.funnycoding.service

import cats.data._
import cats.effect.IO
import org.http4s.{HttpService, Response}
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Location, `Content-Type`}
import org.http4s.{HttpService, MediaType, Uri}


class HttpToElasticService extends Http4sDsl[IO] {
  private implicit val encodeA: Encoder[GetEvent] = io.circe.generic.semiauto.deriveEncoder[GetEvent]

  sealed abstract class Error
  final case class BadStringParam(string: String) extends Error

  case class GetEvent(index: String, eventId: String)

  def validateReq(getEvent: GetEvent): ValidatedNel[Error, GetEvent] = {
    val validIndex = validateStringParam(getEvent.index)
    val validEventId = validateStringParam(getEvent.eventId)

    (validIndex, validEventId).mapN(GetEvent)
  }
  def validateStringParam(stringParam: String): ValidatedNel[Error, String] = {
    val a = for {
      result <- stringParam match {
        case x if x.length >= 1 && x.length < 20 => Validated.valid(x)
        case _ => Validated.invalidNel(BadStringParam(s"stop bullshit man"))
      }
    } yield result
    a
  }

  val service = HttpService[IO] {
    case GET -> Root / "es"/ index / eventId =>
      validateReq(GetEvent(index, eventId)).fold(
            e => BadRequest(),
            ge => Ok(ge.asJson)
          )
  }
}
