import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.Base64

/** Provides functionality for manipulating HTML.
 * 
 * A key component of this includes the @CALLABLE parser, which parses HTML for the word "@CALLABLE"
 * in order to execute the command linked to it and replace the command with HTML
 */
object HtmlGenerator {

  val twoSpaces: String = "  "

  /** Creates an HTML Base64 embedded image from the filepath of an image
   * 
   * @param path the filepath of the image
   * @return an HTML img String with the image represented as Base64
   */
  def makeBase64Image(path: String): String = {
    s"<img src=\"data:jpeg;base64, ${fileToBase64(path)}\"/>\n"
  }

  /** Converts a file to a Base64 String
   *
   * @param filepath the file to be converted
   * @return a String representing the file's contents in Base64
   */
  private def fileToBase64(filepath: String): String = {
    val file = Files.readAllBytes(new File(filepath).toPath)
    Base64.getEncoder.encodeToString(file)
  }

  /** Reads in a CSS file as a String.
   * 
   * @param filepath the filepath of the CSS file
   * @return the entire contents of the CSS file as a String
   */
  def loadCss(filepath: String): String = {
    val file = Files.readAllBytes(new File(filepath).toPath)
    String(file, StandardCharsets.UTF_8)
  }
  
  def parseArgs(command: String): String = {
    command.split("\\(")(1).replace(");", "").replace("\"", "")
  }

  /** Matches a @CALLABLE instance with it's function. Returns a String from the executed @CALLABLE.
   * 
   * @param line the line that is the @CALLABLE instance
   * @return a String representing the HTML generated by the @CALLABLE instance
   */
  def writeCallableCommand(line: String): String = {
    println(line)
    val command = line.split("\\.", 2)(1)
    val function = command.split("\\(")(0)
    function match {
      case "makeBase64Image" => makeBase64Image(parseArgs(command))
      case "loadCss" => loadCss(parseArgs(command))
    }
  }

  private def isCallable(line: String) = line.startsWith("@CALLABLE")

  /** Reads an HTML file and replaces all @CALLABLE instances with their respective HTML generated
   * 
   * @param html the HTML file to be parsed for @CALLABLE instances
   * @return A String that is a new HTML file with the @CALLABLE instances replaced with HTML
   */
  def replaceCallable(html: String): String = {
    val replaced = Files.readAllLines(Path.of(html))
      .stream()
      .map(line => line.trim)
      .map(line => if isCallable(line) then writeCallableCommand(line) else line)
      .toList
    String.join("", replaced)
  }

}