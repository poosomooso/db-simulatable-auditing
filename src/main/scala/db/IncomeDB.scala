package db

import scala.collection.mutable

class IncomeDB {
	private val db = mutable.Map[Int, (String, Int)]()

	def primaryKeys (): Iterable[Int] = db.keys

	def addVal (id:Int, name:String, income:Int): Unit = db(id) = (name, income)

	def printDB (): Unit = {
		println("      ID         Name    Salary")
		for ((id, tup) <- db.toSeq.sortBy(_._1)) {
			println(f"$id%8d ${tup._1}%12s ${tup._2}%9d")
		}
	}

	def getMax (ids:List[Int]): Int = ids.map(id => db(id)._2).max
}