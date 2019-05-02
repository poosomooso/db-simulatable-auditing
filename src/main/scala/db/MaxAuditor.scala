package db

trait MaxAuditor {
	def getMax(ids:List[Int]): Option[Int]
}