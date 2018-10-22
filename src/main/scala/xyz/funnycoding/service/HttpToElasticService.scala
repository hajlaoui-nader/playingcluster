package xyz.funnycoding.service

import cats.data._
import cats.effect.IO
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpService
import xyz.funnycoding.events.GetEvent
import com.sksamuel.elastic4s.http.ElasticDsl._
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
      validateReq(GetEvent(index, eventId))
        .map(repo.fetchEventByIndexAndEventId)
        .fold(
          e =>
            BadRequest(e.map{
              case BadStringParam(err) => err
            }.toList.mkString(",")),
          ge => {
            val v = ge.await
            Ok(v.result.hits.hits.head.sourceAsString)
          }
        )
  }
}
