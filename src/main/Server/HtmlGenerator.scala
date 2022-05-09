import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.Base64

object HtmlGenerator {

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

  def parseArgs(command: String): String = {
    command.split("\\(")(1).replace(");", "").replace("\"", "")
  }

  def writeCallableCommand(line: String): String = {
    println(line)
    val command = line.split("\\.", 2)(1)
    val function = command.split("\\(")(0)
    function match {
      case "makeBase64Image" => makeBase64Image(parseArgs(command))
      case "loadCss" => loadCss(parseArgs(command))
      case "makeStockDataTable" => "shart"
    }
  }

  private def isCallable(line: String) = line.startsWith("@CALLABLE")

  def replaceCallable(request: UrlRequest): String = {
    val replaced = Files.readAllLines(Path.of(request.path))
      .stream()
      .map(line => line.trim)
      .map(line => if isCallable(line) then writeCallableCommand(line) else line)
      .toList
    String.join("", replaced)
  }

}
