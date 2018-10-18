package xyz.funnycoding

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import pureconfig.error.ConfigReaderException

package object config {
  case class ServerConfig(host: String ,port: Int)

 /* case class DatabaseConfig(driver: String, url: String, user: String, password: String)

  case class Config(server: ServerConfig, database: DatabaseConfig)*/
  case class Config(server: ServerConfig)

  object Config {
    import pureconfig._

    def load(configFile: String = "app.conf"): IO[Config] = {
      IO {
        loadConfig[Config](ConfigFactory.load(configFile))
      }.flatMap {
        case Left(e) => IO.raiseError[Config](new ConfigReaderException[Config](e))
        case Right(config) => IO.pure(config)
      }
    }
  }
}