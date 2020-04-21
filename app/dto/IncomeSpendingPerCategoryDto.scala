package dto

import entities.{Category, Id, User}

case class IncomeSpendingPerCategoryDto(
  userId: Id[User],
  categoryId: Id[Category],
  howToPayId: Int,
  amount: Int
)
