package db

object Main {
	def main(args: Array[String]): Unit = {
		smallDBTest
		println
		naiveCompromise
	}

	def naiveCompromise : Unit = {
		println("Naive compromise")
		val db = new IncomeDB()
		db.addVal(0, "Bob", 60000)
		db.addVal(1, "Erin", 80000)
		db.addVal(2, "Marty", 70000)
		db.addVal(3, "Abby", 70000)
		db.printDB()


		val simAuditor = new MaxSimulatableAuditor(db)
		val ksAuditor = new KnowledgeSpaceAuditor(db)

		val auditors = List(ksAuditor, simAuditor)

		makeQuery(auditors, List(0, 1, 2, 3))
		makeQuery(auditors, List(0, 2, 3))
		makeQuery(auditors, List(0, 1, 2))

	}

	def smallDBTest : Unit = {
		println("**Small Test**")
		val db = new IncomeDB()
		db.addVal(0, "Bob", 60000)
		db.addVal(1, "Erin", 80000)
		db.addVal(2, "Marty", 70000)
		db.addVal(3, "Abby", 70000)
		db.printDB()


		val simAuditor = new MaxSimulatableAuditor(db)
		val ksAuditor = new KnowledgeSpaceAuditor(db)

		val auditors = List(ksAuditor, simAuditor)

		makeQuery(auditors, List(1))
		makeQuery(auditors, List(0, 2))
		makeQuery(auditors, List(0,1))
		makeQuery(auditors, List(1, 2))
		makeQuery(auditors, List(0,1,2, 3))
	}

	def makeQuery(auditors:Iterable[MaxAuditor], query:List[Int]): Unit = {
		println("Query: " + query)
		for (a <- auditors) {
			val res:Option[Int] = a.getMax(query)
			println(f"\t${a.getName}%-30s: " + res)
		}
	}
}