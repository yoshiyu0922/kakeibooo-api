package dto

import java.time.LocalDate

import entities.{Budget, Category, CategoryDetail}

case class BudgetSummary(
  month: LocalDate,
  budget: Option[Budget],
  category: Category,
  categoryDetail: CategoryDetail,
  result: Option[IncomeSpendingSummary]
)

object BudgetSummary {

  def fromEntity(
    budget: Option[Budget],
    result: Option[IncomeSpendingSummary],
    categories: List[Category],
    categoryDetail: CategoryDetail,
    month: LocalDate
  ): BudgetSummary = {
    val category =
      categories
        .find(_.categoryId == categoryDetail.categoryId)
        .ensuring(_.isDefined, "category is not found in BudgetResponse.fromEntry")
        .get

    BudgetSummary(
      month = month,
      budget = budget,
      category = category,
      categoryDetail = categoryDetail,
      result = result
    )
  }
}
