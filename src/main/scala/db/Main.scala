package db

object Main {
	def main(args: Array[String]): Unit = {
		smallDBTest()

	}

	def smallDBTest : Unit = {
		val db = IncomeDB()
		db.addVal(0, "Bob", 60000)
		db.addVal(1, "Erin", 80000)
		db.addVal(2, "Marty", 70000)
		db.printDB()

		val simAuditor = MaxSimulatableAuditor(db)

		simAuditor.
	}
}