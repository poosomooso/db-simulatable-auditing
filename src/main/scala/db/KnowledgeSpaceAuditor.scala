package db

import scala.collection.mutable

/**
  * Based off of Chin's "Security problems on inference control for SUM, MAX, and MIN queries" (1986)
  *
  * @param db the IncomeDB being queried.
  */
class KnowledgeSpaceAuditor(val db: IncomeDB) extends MaxAuditor {

  case class Query(ids: Set[Int], op: Operator)

  sealed trait Operator
  object Operator{
    case object EQ extends Operator
    case object LT extends Operator
  }
  import Operator._

  private var knowledgeSpace = Map[Query, Int]()

  override def getName: String = "Knowledge Space Auditor"

  override def getMax(ids: List[Int]): Option[Int] = {
    if (ids.size <= 1) {
      return None
    }

    val nextMax = db.getMax(ids)

    var newKnowledge = knowledgeSpace + (Query(ids.toSet, EQ) -> nextMax)
    var oldKnowledge = newKnowledge
    do { // when was the last time you saw a do while loop??
      oldKnowledge = newKnowledge
      newKnowledge = expandKnowledge(oldKnowledge)
    } while (oldKnowledge.size != newKnowledge.size)


    val numIdentified = containsIdentifiedElements(newKnowledge)
    if (numIdentified) return None

    knowledgeSpace = newKnowledge
//    println(knowledgeSpace)
    Some(nextMax)
  }

  def expandKnowledge(oldKnowledge: Map[Query, Int]): Map[Query, Int] = {
    val newKnowledge = mutable.Map[Query, Int]()

    // apply a bunch of rules enumerated in the paper
    for ((q1, m1) <- oldKnowledge; (q2, m2) <- oldKnowledge) {
      if (q1 != q2) {
        q1.op match {
          case EQ =>
            q2.op match {
              case EQ => {
                if (m1 == m2) {
                  val newq1 = Query(q1.ids & q2.ids, EQ)
                  newKnowledge += (newq1 -> m1)
                  val newq2 = Query((q1.ids -- q2.ids) ++ (q2.ids -- q1.ids), LT)
                  newKnowledge += (newq2 -> m1)
                } else if (m1 > m2) {
                  newKnowledge += (Query(q1.ids -- q2.ids, EQ) -> m1)
                  newKnowledge += (Query(q2.ids, EQ) -> m2)
                } else {
                  newKnowledge += (Query(q2.ids -- q1.ids, EQ) -> m2)
                  newKnowledge += (Query(q1.ids, EQ) -> m1)
                }
              }
              case LT => {
                if (m1 >= m2) {
                  newKnowledge(Query(q1.ids -- q2.ids, EQ)) = m1
                  newKnowledge(Query(q2.ids, LT)) = m2
                } else {
                  newKnowledge(Query(q1.ids, EQ)) = m1
                  newKnowledge(Query(q2.ids -- q1.ids, LT)) = m2
                }
              }
            }
          case LT =>
            q2.op match {
              case EQ => {
                if (m1 <= m2) {
                  newKnowledge(Query(q2.ids -- q1.ids, EQ)) = m2
                  newKnowledge(Query(q1.ids, LT)) = m1
                } else {
                  newKnowledge(Query(q2.ids, EQ)) = m2
                  newKnowledge(Query(q1.ids -- q2.ids, LT)) = m1
                }
              }
              case LT => {
                if (m1 == m2) {
                  newKnowledge(Query(q1.ids ++ q2.ids, LT)) = m1
                } else if (m1 > m2) {
                  newKnowledge(Query(q1.ids -- q2.ids, LT)) = m1
                  newKnowledge(Query(q2.ids, LT)) = m2
                } else {
                  newKnowledge(Query(q2.ids -- q1.ids, LT)) = m2
                  newKnowledge(Query(q1.ids, LT)) = m1
                }
              }
            }
        }
      }

      newKnowledge(q1) = m1
      newKnowledge(q2) = m2
    }

    return newKnowledge.toMap

  }

  def containsIdentifiedElements(ks: Map[Query, Int]): Boolean = {
    ks.count { case (q, m) => q.ids.size == 1 && q.op == EQ } > 0
  }
}

