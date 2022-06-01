# digital-engagement-platform-chat

Chat library plugin to regime service which calls partials service to retrieve page
content / skin service to retrieve either the Nuance chatskin or HMRC chatskin.

The capability to pull the HMRC chatskin was introduced with version "0.25.0-play-28".

The decision on what type of chatskin to use is made on a frontend service by calling one of
the following methods in the WebChatClientImpl class.

### Webchat and Digital Assistent Version 1
loadRequiredElements()

loadWebChatContainer(id)

### Webchat and Digital Assistent Version 2
loadRequiredElements()

loadWebChatContainer(id)

### HMRC Webchat and Digital Assistent Version 3
loadRequiredElements()
loadWebChatContainer(id)

and one of the following

loadHMRCChatSkinElement("embedded")

loadHMRCChatSkinElement("popup")

### Run the application
The application runs on port 9193

To run all the DEP services executed the following command
**sm --start DIGITAL_ENGAGEMENT_PLATFORM_ALL -r**

To run digital-engagement-platform-chat locally:
run **sm --stop digital-engagement-platform-chat**
run **sbt run**

### Testing
To run the unit test use **sbt test**


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
