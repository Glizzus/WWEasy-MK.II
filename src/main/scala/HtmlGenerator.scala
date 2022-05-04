case class HtmlGenerator() {

  val twoSpaces: String = "  "

  def topBoilerPlate(titleContent: String): String = {
    "<!DOCTYPE html>" +
      twoSpaces + wrap("head")(
        "  <meta charset=\"UTF-8\">" +
        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
        "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
          twoSpaces * 2 + wrap("title")(titleContent) +
      "  <body>"
      )
  }

  val bottomBoilerPlate: String = {
    "  </body>" +
      "</html>"
  }


  def wrap(tag: String)(content: String): String = {
    s"<$tag>$content</$tag>"
  }

  def wrapDiv(content: String): String = wrap("div")(content)

  def wrapP(content: String): String = wrap("p")(content)

  def attributeWrap(tag: String)(attributes: Seq[String])(content: String): String = {
    s"<$tag ${attributes.mkString(" ")}>$content</$tag>"
  }

  def attributeWrapDiv(attributes: Seq[String])(content: String): String = {
    attributeWrap("div")(attributes)(content)
  }

}
