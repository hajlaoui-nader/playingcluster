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
import xyz.funnycoding.events.GetEvent
import xyz.funnycoding.repository.EsRepository

class HttpToElasticService(repo: EsRepository) extends Http4sDsl[IO] {

  sealed abstract class Error
  final case class BadStringParam(err: String) extends Error

  def validateReq(getEvent: GetEvent): ValidatedNel[Error, GetEvent] = {
    val validIndex = validateStringParam(getEvent.index)
    val validEventId = validateStringParam(getEvent.eventId)

    (validIndex, validEventId).mapN(GetEvent)
  }
  def validateStringParam(stringParam: String): ValidatedNel[Error, String] = for {
      result <- stringParam match {
        case x if x.length >= 1 && x.length < 20 => Validated.valid(x)
        case _ => Validated.invalidNel(BadStringParam(s"stop bullshit man"))
      }
    } yield result


  val service = HttpService[IO] {
    case GET -> Root / "es"/ index / eventId =>
      validateReq(GetEvent(index, eventId)).fold(
            e =>
              BadRequest(e.map{
                case BadStringParam(err) => err
              }.toList.mkString(","))
            ,
            ge => Ok(ge.asJson)
          )
  }
}
