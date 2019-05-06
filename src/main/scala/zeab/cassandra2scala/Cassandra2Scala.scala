package zeab.cassandra2scala

//Imports
import zeab.sys.EnvironmentVariables
//Java
import java.io.{File, PrintWriter}
//Scala
import scala.io.Source

//TODO make the compares tail recursive

object Cassandra2Scala extends App with EnvironmentVariables {

  val filesToReadFrom: String =
    modifyFileNamePerSystem(getEnvVar[String]("FILES_TO_READ"))

  val filesToWriteTo: String =
    modifyFileNamePerSystem(getEnvVar[String]("FILES_TO_WRITE"))

  val files: List[File] = getListOfFiles(filesToReadFrom)

  files.foreach { file =>
    //Open up all the files in a given directory
    val lines: List[String] = Source.fromFile(file).getLines.toList

    //Grabs the table name from the
    val tableName: String =
      lines.headOption.getOrElse("X")
        .replace("CREATE TABLE ", "")
        .replace(" (", "")
        .split('.').toList.drop(1)
        .headOption.getOrElse("X")
        .split("_").toList
        .map { line => line(0).toUpper + line.drop(1) }
        .mkString

    val tableModel: List[List[String]] =
      lines
        .drop(1)
        .takeWhile ( _.headOption.getOrElse("") != ')' )
        .filterNot ( _.contains("    PRIMARY KEY (") )
        .map ( _.replace("PRIMARY KEY", "") )
        .map ( _.replace("frozen", "") )
        .map ( _.trim )
        .map { line =>
          if (line.endsWith(",")) line.dropRight(1)
          else line
        }
        .map ( _.split(" ", 2).toList )

    val allowNulls: Boolean = false

    val caseClass: String =
      tableModel
        .map { line =>
          val key: String = line.headOption.getOrElse("").trim
          val value: String = line.lastOption.getOrElse("").trim
          value match {
            case "text" => s"$key: String"
            case "bigint" => s"$key: Long"
            case "int" => s"$key: Int"
            case "boolean" => s"$key: Boolean"
            case s: String =>
              if (s.startsWith("<tuple")) {
                s.replace("<tuple<", "")
                  .replace(">>", "")
                  .split(',').map ( _.trim )
                  .map {
                    case "text" => s"String"
                    case "bigint" => s"Long"
                    case "int" => s"Int"
                    case "boolean" => s"Boolean"
                  }.mkString(s"$key: (", ", ", ")")
              }
              else s"$key: ???"
            case _ => s"$key: ???"
          }
        }.mkString(s"case class ${tableName}Row (\n", ",\n", "\n)")

    println(caseClass)
    println()

    //Write it to file
    new PrintWriter(s"$filesToWriteTo\\${tableName}Row.scala") {
      write(caseClass)
      close()
    }

    val serializationFunction =
      tableModel
        .map { line =>
          val key: String = line.headOption.getOrElse("").trim
          val value: String = line.lastOption.getOrElse("").trim
          value match {
            case "text" => s"row.getString($key)"
            case "bigint" => s"row.getLong($key)"
            case "int" => s"row.getInt($key)"
            case "boolean" => s"row.getBool($key)"
            case s: String =>
              if (s.startsWith("<tuple")) {
                s.replace("<tuple<", "")
                  .replace(">>", "")
                  .split(',').map {
                  _.trim
                }
                  .map {
                    case "text" => s"String"
                    case "bigint" => s"Long"
                    case "int" => s"Int"
                    case "boolean" => s"Boolean"
                  }.mkString(s"$key: (", ", ", ")")
              }
              else s"0//row.get???($key)"
            case _ => s"row.getInt($key)"
          }
        }.mkString(s"${tableName}Row(\n", ",\n", "\n)")

    println(serializationFunction)

  }

  def getListOfFiles(dir: String): List[File] = {
    val d: File = new File(dir)
    if (d.exists && d.isDirectory) d.listFiles.filter(_.isFile).toList
    else List[File]()
  }

  //TODO Make it mac and linux replacement stuff...
  def modifyFileNamePerSystem(dir: String): String =
    dir.replace("""\""", """\\""")

}
