package caches

sealed abstract class HowToPay(val id: Int, val name: String)

object HowToPay {
  case object Card extends HowToPay(1, "現金払い")
  case object Cache extends HowToPay(2, "カード払い")

  lazy val list = List(Card, Cache)

  def nameById(idOpt: Option[Int]): String =
    idOpt.fold("収入")(id => list.find(_.id == id).get.name)
}
