package xyz.funnycoding.http

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties, Response}
import com.sksamuel.elastic4s.http.ElasticDsl._

object PushSomeDataToES extends App {

  import com.sksamuel.elastic4s.http.cluster.ClusterStateResponse

  import scala.concurrent.Future

  val client = ElasticClient(ElasticProperties("http://localhost:9200"))

  client.execute {
    bulk(
      indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
      indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
    ).refresh(RefreshPolicy.WaitFor)
  }.await

  val response: Response[SearchResponse] = client.execute {
    search("myindex").matchQuery("capital", "ulaanbaatar")
  }.await

  // prints out the original json
  println(response.result.hits.hits.head.sourceAsString)

  val resp: Response[ClusterStateResponse] = client.execute {
    clusterState
  }.await
  println(resp.result.metadata.map(_.indices).map(_.keys))

  client.close()


}
