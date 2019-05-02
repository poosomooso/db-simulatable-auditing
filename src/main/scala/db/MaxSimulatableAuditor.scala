package db

class MaxSimulatableAuditor(val db:DBInterface) extends MaxAuditor {

	private var prevQueries = List[(Set[Int], Int)]()
	private var upperBounds = HashMap[Int, Int]()

	def getMax(ids:List[Int]): Option[Int] = {
		val idSet = ids.toSet
		val relevantQueries:List[(Set[Int], Int)] = prevQueries.filter(_._1.intersect(idSet).size > 0)
		val sortedAnswers:List[Int] = relevantQueries.map(_._2).sorted
		val possibleAnswers:List[Int] = (sortedAnswers.head - 1) :: addBoundMidponts(sortedAnswers) :: (sortedAnswers.last + 1)
		
		for (m:Int <- possibleAnswers) {
			val newBound:Map[Int, Int] = ids.map(i => (i, min(m, upperBounds[i]))).toMap()
			var extremeCount = 0
			for ((i, b) <- newBound) {
				if (b == m) {
					extremeCount++
				}
			}

			if (extremeCount == 1) {
				return None
			}

			val tempUpperBounds = upperBounds ++ newBound

			for ((qIDS, qm) <- prevQueries) {
				// for each element in qIDS
					//if extreme, inc

				// if extreme == 1
					//return None
			}
		}
		
		val max = db.getMax(ids)
		prevQueries(idSet) = max
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
}