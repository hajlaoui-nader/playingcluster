scalacOptions += "-Ypartial-unification"

lazy val Elastic4sVersion = "6.3.3"
lazy val Http4sVersion = "0.18.7"

lazy val DoobieVersion = "0.5.2"

lazy val H2Version = "1.4.192"

lazy val FlywayVersion = "4.2.0"

lazy val CirceVersion = "0.9.3"

lazy val PureConfigVersion = "0.9.1"

lazy val LogbackVersion = "1.2.3"

lazy val ScalaTestVersion = "3.0.4"

lazy val ScalaMockVersion = "4.0.0"

lazy val root = Project("playingcluster", file("."))
  .configs(IntegrationTest)
  .settings(name := "playingcluster")
  .settings(scalaVersion := "2.12.7")
  .settings(libraryDependencies ++= Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-http" % Elastic4sVersion,

    "org.http4s"            %% "http4s-blaze-server"  % Http4sVersion,
    "org.http4s"            %% "http4s-circe"         % Http4sVersion,
    "org.http4s"            %% "http4s-dsl"           % Http4sVersion,
    "org.http4s"            %% "http4s-blaze-client"  % Http4sVersion     % "it,test",

    "org.tpolecat"          %% "doobie-core"          % DoobieVersion,
    "org.tpolecat"          %% "doobie-h2"            % DoobieVersion,
    "org.tpolecat"          %% "doobie-hikari"        % DoobieVersion,

    "com.h2database"        %  "h2"                   % H2Version,

    "org.flywaydb"          %  "flyway-core"          % FlywayVersion,

    "io.circe"              %% "circe-generic"        % CirceVersion,
    "io.circe"              %% "circe-literal"        % CirceVersion      % "it,test",
    "io.circe"              %% "circe-optics"         % CirceVersion      % "it",

    "com.github.pureconfig" %% "pureconfig"           % PureConfigVersion,

    "ch.qos.logback"        %  "logback-classic"      % LogbackVersion,

    "org.scalatest"         %% "scalatest"            % ScalaTestVersion  % "it,test",
    "org.scalamock"         %% "scalamock"            % ScalaMockVersion  % "test"
  ))