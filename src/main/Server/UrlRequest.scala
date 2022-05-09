import scala.util.matching.Regex

object UrlRequest {

  def requestFromUrl(url: String): UrlRequest = {

    val RootDirectory = "src/web/views"

    val splitUrl: Array[String] = url.split(" ")
    val method: String = splitUrl(0)
    val relRequest: String = splitUrl(1)

    val path = RootDirectory + pathFromRelativeRequest(relRequest)
    val query = queryFromRelativeRequest(relRequest)
    UrlRequest(method, path, query)
  }

  def pathFromRelativeRequest(relRequest: String): String = {

    val hasArgs: String => Boolean = url => url.contains("?")
    val invalidPath: String => Boolean = url => url.contains("..")

    if invalidPath(relRequest) | relRequest == "/" then {
      "/index.html"
    }
    else if !hasArgs(relRequest) then {
      relRequest
    }
    else {
      relRequest.split("\\?")(0)
    }
  }

  def queryFromRelativeRequest(relRequest: String): Array[String] = {
    val afterQuestionMark = "\\?(.*)".r
    val query = afterQuestionMark.findFirstIn(relRequest) match {
      case Some(string) => string.replaceFirst("\\?", "").split("&")
      case None => Array[String]()
    }
    query
  }
}


case class UrlRequest(method: String, path: String, query: Array[String]) {

  val isPost: Boolean = method.equalsIgnoreCase("post")
  val isGet: Boolean = method.equalsIgnoreCase("get")

}
