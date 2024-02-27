resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.20.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.3")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.1")

