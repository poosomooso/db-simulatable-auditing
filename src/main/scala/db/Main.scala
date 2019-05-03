package db

object Main {
	def main(args: Array[String]): Unit = {
		smallDBTest
	}

	def smallDBTest : Unit = {
		val db = new IncomeDB()
		db.addVal(0, "Bob", 60000)
		db.addVal(1, "Erin", 80000)
		db.addVal(2, "Marty", 70000)
		db.addVal(3, "Abby", 70000)
		db.printDB()

		println("**Auditor 1**")
		var simAuditor = new MaxSimulatableAuditor(db)

		makeQuery(simAuditor, List(1,2))
		makeQuery(simAuditor, List(0,1,2))
		makeQuery(simAuditor, List(0,1,2, 3))
		makeQuery(simAuditor, List(0,2))
		makeQuery(simAuditor, List(0))

		println
		println("**Auditor 2**")
		simAuditor = new MaxSimulatableAuditor(db)

		makeQuery(simAuditor, List(0, 2))
		makeQuery(simAuditor, List(0,1))
		makeQuery(simAuditor, List(1, 2))
		makeQuery(simAuditor, List(0,1,2, 3))
	}

	def makeQuery(auditor:MaxAuditor, query:List[Int]): Option[Int] = {
		println("Query: " + query)
		val res:Option[Int] = auditor.getMax(query)
		println("\tResult: " + res)
		return res
	}
}