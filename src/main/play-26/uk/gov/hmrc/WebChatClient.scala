package uk.gov.hmrc.client

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc.Request
import play.twirl.api.Html


class WebChatClient @Inject()(configuration: Configuration) {
  def getElements()(implicit request: Request[_]): Html = {

  }
}
