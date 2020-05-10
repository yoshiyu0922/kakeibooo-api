package dto

import entities.{CategoryDetail, Id, User}

case class IncomeSpendingSummary(
  userId: Id[User],
  categoryDetailId: Id[CategoryDetail],
  howToPayId: Int,
  amount: Int
)
