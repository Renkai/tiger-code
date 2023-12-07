import scala.collection.mutable
import scala.io.Source

object Reorder {
  def main(args: Array[String]): Unit = {
    val file = Source.fromFile("tigress.dict.yaml")
    val map = mutable.Map.empty[String, (Int, String)]
    file.getLines().dropWhile(x => !x.startsWith("的")).foreach {
      line =>
        //        println(s"line = ${line}")
        val Array(text, weight, code) = line.split("\t").take(3)
        val weightInt = weight.toInt
        val (currentWeight, currentCode) = map.getOrElse(text, (0, ""))
        val next = if (weightInt >= currentWeight) {
          (weightInt, code)
        } else (currentWeight, currentCode)
        map(text) = next
    }
    val topChars = map.toSeq.sortBy(- _._2._1)
    val top500 = topChars.map(_._1).take(500)
    val top1000 = topChars.map(_._1).take(1000)

    val sortedTop500 = top500.sortBy(x => map(x)._2)
//    println(sortedTop500)
//    sortedTop500.foreach(x => println(map(x)._2))

    val sortedTop1000 = top1000.sortBy(x => map(x)._2)
    println(sortedTop1000.mkString(""))
//    sortedTop1000.foreach(x => println(map(x)._2))
  }
}
