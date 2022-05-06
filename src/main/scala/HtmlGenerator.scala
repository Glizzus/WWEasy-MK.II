import java.util.Base64
import java.nio.file.Files
import java.io.File
import scala.io.Source
import java.nio.file.Path
import java.nio.charset.StandardCharsets

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

  def wrapTable(content: String): String = wrap("table")(content)

  def wrapTr(content: String): String = wrap("tr")(content)

  def wrapTd(content: String): String = wrap("td")(content)

  def attributeWrap(tag: String)(attributes: Seq[String])(content: String): String = {
    s"<$tag ${attributes.mkString(" ")}>$content</$tag>"
  }

  def attributeWrapDiv(attributes: Seq[String])(content: String): String = {
    attributeWrap("div")(attributes)(content)
  }

  def makeBase64Image(path: String): String = {
    s"<img src=\"data:jpeg;base64, ${fileToBase64(path)}\"/>\n"
  }

  def fileToBase64(filepath: String): String = {
    val file = Files.readAllBytes(new File(filepath).toPath)
    Base64.getEncoder.encodeToString(file)
  }

  def loadCss(filepath: String): String = {
    val file = Files.readAllBytes(new File(filepath).toPath)
    String(file, StandardCharsets.UTF_8)
  }


  def writeCallableCommand(line: String): String = {
    val command = line.split("\\.", 2)(1)
    val args = command.split("\\(")(1).replace(");", "").replace("\"", "")
    command match {
      case command if command.startsWith("makeBase64Image") => HtmlGenerator().makeBase64Image(args)
      case command if command.startsWith("loadCss") => HtmlGenerator().loadCss(args)
    }
  }

    def parseForCallable(filepath: String): String = {
      val replaced = Files.readAllLines(Path.of(filepath))
        .stream().map(line => if line.trim.startsWith("@CALLABLE") then writeCallableCommand(line.trim) else line).toList
      String.join("", replaced)
    }

}
