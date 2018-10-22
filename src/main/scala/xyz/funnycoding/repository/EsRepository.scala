package xyz.funnycoding.repository

import com.sksamuel.elastic4s.http.ElasticDsl.search
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties, Response}
import xyz.funnycoding.events.GetEvent
import scala.concurrent.Future
import com.sksamuel.elastic4s.http.ElasticDsl._

class EsRepository {
  lazy val client: ElasticClient = ElasticClient(ElasticProperties("http://localhost:9200"))


  def fetchEventByIndexAndEventId(getEvent: GetEvent): Future[Response[SearchResponse]] = {
    client.execute {
      search(getEvent.index).matchQuery("eventId", getEvent.eventId)
    }
  }

}
