package dto.response.budget

import java.time.LocalDate

import dto.IncomeSpendingPerCategoryDto
import entities.{Budget, Category, CategoryDetail}
import play.api.libs.json.{Format, Json}

case class ListResponse(list: List[BudgetResponse])

object ListResponse {
  implicit val format: Format[ListResponse] = Json.format

  /**
    * カテゴリ数分の予算を作成
    *   - 作成されている予算は紐づくカテゴリに設定する
    *   - 未設定の予算のカテゴリは空で設定する
    *
    * @param budgets 予算リスト
    * @param results 実績リスト
    * @param categories 親カテゴリ
    * @param categoryDetails カテゴリ
    * @param month 予算月
    * @return ListResponse
    */
  def fromEntities(
    budgets: List[Budget],
    results: List[IncomeSpendingPerCategoryDto],
    categories: List[Category],
    categoryDetails: List[CategoryDetail],
    month: LocalDate
  ): ListResponse = {
    // 設定済みの予算を設定
    val responseOfBudget = budgets.map(budget => {
      val category = categoryDetails
        .find(_.categoryDetailId == budget.categoryDetailId)
        .ensuring(_.isDefined, "[budget] category is not found")
        .get
      BudgetResponse
        .fromEntity(Option(budget), results, categories, category, month)
    })

    // 未設定の予算に紐づくカテゴリの予算を作成
    val responses: List[BudgetResponse] = categoryDetails
      .filterNot(c => budgets.exists(_.categoryDetailId == c.categoryDetailId))
      .map(category => {
        BudgetResponse
          .fromEntity(None, results, categories, category, month)
      })
    ListResponse(list = List(responseOfBudget, responses).flatten)
  }
}
