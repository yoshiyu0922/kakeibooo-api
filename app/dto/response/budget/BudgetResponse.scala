package dto.response.budget

import java.time.LocalDate

import caches.HowToPay
import dto.IncomeSpendingPerCategoryDto
import entities.{Budget, BudgetDetail, Category, CategoryDetail}
import play.api.libs.json.{Format, Json}

case class BudgetResponse(
  budgetId: Option[Long],
  categoryId: Long,
  categoryName: String,
  budgetMonth: LocalDate,
  isIncome: Boolean,
  content: Option[String],
  details: List[BudgetDetailResponse]
)

object BudgetResponse {
  implicit val format: Format[BudgetResponse] = Json.format
  def fromEntity(
    budget: Option[Budget],
    results: List[IncomeSpendingPerCategoryDto],
    parentCategories: List[Category],
    categoryDetail: CategoryDetail,
    month: LocalDate
  ): BudgetResponse = {
    val parentCategory =
      parentCategories.find(_.categoryId == categoryDetail.categoryId)
    val isIncome = parentCategory.exists(_.isIncome)

    // 支払い方法リストに紐づく予算と実績を作成
    val details: List[BudgetDetailResponse] = HowToPay.list.map(payMethod => {
      val detail =
        budget.flatMap(_.details.find(d => d.howToPayId.contains(payMethod.id)))
      val result =
        results.find(
          r => r.categoryId == categoryDetail.categoryDetailId && r.howToPayId == payMethod.id
        )
      BudgetDetailResponse
        .fromEntity(detail, result, categoryDetail, payMethod.id)
    })

    BudgetResponse(
      budgetId = budget.map(_.budgetId.value),
      categoryId = categoryDetail.categoryId.value,
      categoryName = categoryDetail.name,
      budgetMonth = month,
      isIncome = isIncome,
      content = budget.map(_.content),
      details = details
    )
  }
}

case class BudgetDetailResponse(amount: Int, resultAmount: Int, howToPayId: Int)

object BudgetDetailResponse {
  implicit val format: Format[BudgetDetailResponse] = Json.format

  def fromEntity(
    detail: Option[BudgetDetail],
    result: Option[IncomeSpendingPerCategoryDto],
    category: CategoryDetail,
    howToPayId: Int
  ): BudgetDetailResponse =
    BudgetDetailResponse(
      amount = detail.map(_.amount).getOrElse(0),
      resultAmount = result.map(_.amount).getOrElse(0),
      howToPayId = howToPayId
    )
}
