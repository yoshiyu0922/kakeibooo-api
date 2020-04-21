package dto

import entities.Budget

case class BudgetDto(
  budget: Budget,
  resultOpt: Option[IncomeSpendingPerCategoryDto]
)
