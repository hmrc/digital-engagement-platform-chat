# digital-engagement-platform-chat

## About
This library can be used to help with the integration of Nuance's Digital Assistant(DA) and Webchat. Services that wish to provide an embedded or popup DA/Webchat on their pages should make use of this library to ensure they have the required HTML elements.

The library provides a separate call for each through a single interface:

WebChatClient Interface
```
trait WebChatClient {
def loadRequiredElements()(implicit request: Request[_]): Option[Html]
def loadHMRCChatSkinElement(partialType: String)(implicit request: Request[_]): Option[Html]
def loadWebChatContainer(id: String = "HMRC_Fixed_1")(implicit request: Request[_]) : Option[Html]
}
```
Each call returns a block of HTML to be inserted into a page.

loadRequiredElements and loadWebChatContainer are methods which a frontend service should use regardless of the type of DA/Webchat required. loadHMRCChatSkinElement should only be used if the service would like to use HMRC's customised skin.

## Running from source
Clone the repository using SSH:

`git@github.com:hmrc/digital-engagement-platform-chat.git`

cd into your digital-engagement-platform.chat directory

`sbt publishLocal`

This will publish a local copy of the library to your local ivy cache. Take note of the SNAPSHOT version created.

Update AppDependencies.scala on the service you're testing the library on to use the SNAPSHOT version now held in your local ivy cache.

Example
`"uk.gov.hmrc"     %% "digital-engagement-platform-chat" % "0.28.0-play-28-SNAPSHOT"`

NOTE - REMEMBER TO REVERT THIS BACK WHEN FINISHED LOCAL DEVELOPMENT!

If this does not work try adding the following resolver to plugins.sbt
```
resolvers += (
  "Local Maven Repository".at(s"file:///home/yourpath/.ivy2/local")
  )
```

This library requires the digital-engagement-platform-partials to be running locally. This can be done by running the following service manager command:
`sm --start DIGITAL_ENGAGEMENT_PLATFORM_PARTIALS -r`

## Running through Service Manager
Ensure your service manager config is up to date, and run the following command:

`sm --start DIGITAL_ENGAGEMENT_PLATFORM_PARTIALS -r`

Add the following dependency to the AppDependencies.scala file of the service requiring the Nuance DA/webchat (check latest version):
`"uk.gov.hmrc"     %% "digital-engagement-platform-chat" % "X.XX.X-play-XX"`


## Testing
To run the unit tests use: 
`sbt test`

## License
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
