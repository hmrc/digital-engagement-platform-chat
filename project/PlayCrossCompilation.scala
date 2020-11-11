
import uk.gov.hmrc.playcrosscompilation.AbstractPlayCrossCompilation
import uk.gov.hmrc.playcrosscompilation.PlayVersion._

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play27) {
  def version: String = playVersion match {
    case Play26 => "2.6.25"
    case Play27 => "2.7.5"
    case _ => throw new RuntimeException(s"Unsupported play version: $playVersion")
  }
}
