import java.io.{File, PrintWriter}
import scala.collection.mutable
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    val file = Source.fromFile("tigress_ci.dict.yaml")
    val map = mutable.Map.empty[String, Seq[(Int, String)]]
    file.getLines().dropWhile(x => !x.startsWith("我们")).foreach {
      line =>
        //        println(s"line = ${line}")
        val Array(text, weight, code) = line.split("\t")
        val current = map.getOrElse(code, Seq.empty)
        val next = current :+ (weight.toInt, text)
        map(code) = next
    }
    val filteredMap = mutable.Map.empty[String, Seq[(Int, String)]]
    val droppedMap = mutable.Map.empty[String, Seq[(Int, String)]]

    map.foreach {
      case (code, seq) =>
        val sorted = seq.sortWith {
          case ((weight1, text1), (weight2, text2)) =>
            if (text1.length > 4 && text2.length <= 4) false
            else if (text1.length <= 4 && text2.length > 4) true
            else if (text1.length == 3 && text2.length != 3) false
            else if (text1.length != 3 && text2.length == 3) true
            else (weight1 > weight2)
        }
        var filtered = Seq.empty[(Int, String)]
        var dropped = Seq.empty[(Int, String)]
        sorted.foreach {
          case (weight, text) =>
            if (text.length > 4 || filtered.length >= 2) {
              dropped :+= (weight, text)
            } else {
              filtered :+= (weight, text)
            }
        }
        filteredMap(code) = filtered
        droppedMap(code) = dropped
        println(s"keep: ${filteredMap(code)}")
        println(s"    drop: ${droppedMap(code)}")
    }
    val writer = new PrintWriter(new File("filtered.yaml"))
    filteredMap.foreach {
      case (code, seq) =>
        seq.foreach {
          case (weight, text) =>
            writer.write(s"${text}\t${weight}\t${code}\n")
        }
    }
    val writer2 = new PrintWriter(new File("dropped.yaml"))
    droppedMap.foreach {
      case (code, seq) =>
        seq.foreach {
          case (weight, text) =>
            writer2.write(s"${text}\t${weight}\t${code}\n")
        }
    }
  }
}
