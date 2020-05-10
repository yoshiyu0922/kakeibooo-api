package dto.response

import java.time.LocalDate

import dto.IncomeSpendingSummary
import entities.{Budget, Category, CategoryDetail}

case class BudgetResponse(
  month: LocalDate,
  budget: Option[Budget],
  category: Category,
  categoryDetail: CategoryDetail,
  result: Option[IncomeSpendingSummary]
)

object BudgetResponse {

  def fromEntity(
    budget: Option[Budget],
    result: Option[IncomeSpendingSummary],
    categories: List[Category],
    categoryDetail: CategoryDetail,
    month: LocalDate
  ): BudgetResponse = {
    val category =
      categories
        .find(_.categoryId == categoryDetail.categoryId)
        .ensuring(_.isDefined, "category is not found in BudgetResponse.fromEntry")
        .get

    BudgetResponse(
      month = month,
      budget = budget,
      category = category,
      categoryDetail = categoryDetail,
      result = result
    )
  }
}
