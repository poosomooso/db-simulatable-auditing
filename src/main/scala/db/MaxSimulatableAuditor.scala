package db

/**
	* Based off of Kenthapadi et. al's "Simulatable Auditing" (2005).
	* @param db the IncomeDB being queried.
	*/
class MaxSimulatableAuditor(val db:IncomeDB) extends MaxAuditor {

	private var prevQueries = List[(Set[Int], Int)]()
	private var upperBounds = Map[Int, Int]()

	override def getName: String = "Simulatable Auditor"

	override def getMax(ids:List[Int]): Option[Int] = {
		if (ids.size <= 1) {
			return None
		}

		val idSet = ids.toSet
		val relevantQueries:List[(Set[Int], Int)] = prevQueries.filter(_._1.intersect(idSet).nonEmpty)

		if (relevantQueries.nonEmpty) {
			val sortedAnswers:List[Int] = relevantQueries.map(_._2).sorted
			val possibleAnswers:List[Int] = (sortedAnswers.head - 1) :: (addBoundMidponts(sortedAnswers) :+ (sortedAnswers.last + 1))
			
			for (m:Int <- possibleAnswers) {
				val newBound:Map[Int, Int] = 
					ids.map(i => 
							(i, Math.min(m, (if (upperBounds.contains(i)) upperBounds(i) else m))))
						.toMap
				val tempUpperBounds = upperBounds ++ newBound

				var extremeCount = numExtremeElements(ids, tempUpperBounds, m)

				if (extremeCount == 1) {
					return None
				}

				for ((qIDs, qm) <- prevQueries) {
					extremeCount = numExtremeElements(qIDs, tempUpperBounds, qm)

					if (extremeCount == 1) {
						return None
					}
				}
			}
		}
		
		val max = db.getMax(ids)
		prevQueries = (idSet, max) :: prevQueries
		upperBounds = upperBounds ++ ids.map(i => (i, max)).toMap
		return Some(max)
	}

	private def addBoundMidponts(sortedAnswers:List[Int]): List[Int] = {
		sortedAnswers match {
			case a :: rest => 
				rest match {
					case b :: _ => a :: (a + b) / 2 :: addBoundMidponts(rest)
					case Nil =>  List(a)
				}
			case Nil => List()
		}
	}

	private def numExtremeElements(ids:Iterable[Int], currBounds:Map[Int, Int], m:Int): Int = {
		var extremeCount = 0

		for (id:Int <- ids) {
			if (currBounds(id) == m) {
				extremeCount += 1
			}
		}

		return extremeCount
	}
}