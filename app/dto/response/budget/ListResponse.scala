package dto.response.budget

import java.time.LocalDate

import dto.IncomeSpendingPerCategoryDto
import entities.{Budget, Category, ParentCategory}
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
    * @param parentCategories 親カテゴリ
    * @param categories カテゴリ
    * @param month 予算月
    * @return ListResponse
    */
  def fromEntities(
    budgets: List[Budget],
    results: List[IncomeSpendingPerCategoryDto],
    parentCategories: List[ParentCategory],
    categories: List[Category],
    month: LocalDate
  ): ListResponse = {
    // 設定済みの予算を設定
    val responseOfBudget = budgets.map(budget => {
      val category = categories
        .find(_.categoryId == budget.categoryId)
        .ensuring(_.isDefined, "[budget] category is not found")
        .get
      BudgetResponse.fromEntity(Option(budget), results, parentCategories, category, month)
    })

    // 未設定の予算に紐づくカテゴリの予算を作成
    val responses: List[BudgetResponse] = categories
      .filterNot(c => budgets.exists(_.categoryId == c.categoryId))
      .map(category => {
        BudgetResponse.fromEntity(None, results, parentCategories, category, month)
      })
    ListResponse(list = List(responseOfBudget, responses).flatten)
  }
}
