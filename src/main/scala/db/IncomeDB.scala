package db

import scala.collection.mutable.Map

class IncomeDB {
	private val db = Map[Int, (String, Int)]()

	def primaryKeys (): Iterable[Int] = 
		return db.keys

	def addVal (id:Int, name:String, income:Int): Unit =
		db(id) = (name, income)

	def printDB (): Unit = {
		println("      ID         name    income")
		for ((id, tup) <- db) {
			println(f"$id%8d ${tup._1}%12s ${tup._2}%9d")
		}
	}

	def getMax (ids:List[Int]): Int =
		return ids.map(id => db(id)._2).max
}