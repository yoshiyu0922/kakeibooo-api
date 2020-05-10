package dto

import entities.{CategoryDetail, Id, User}

case class IncomeSpendingPerCategoryDto(
  userId: Id[User],
  categoryId: Id[CategoryDetail],
  howToPayId: Int,
  amount: Int
)
