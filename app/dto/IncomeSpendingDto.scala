package dto

import entities.{Account, IncomeSpending}

case class IncomeSpendingDto(
  incomeSpending: IncomeSpending,
  account: Account
)
