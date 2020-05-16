package services

import java.time.LocalDate

import dto.{BudgetSummary, IncomeSpendingSummary}
import dto.response.MutationResponse
import entities._
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import repositories._
import scalikejdbc.DB

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BudgetService @Inject()(
  val repository: BudgetRepository,
  val detailRepository: BudgetDetailRepository,
  val incomeSpendingRepository: IncomeSpendingRepository,
  val masterCache: MasterCache
)(implicit ec: ExecutionContext) {

  /**
    * 指定した期間の予算を検索する
    *
    * - カテゴリ詳細ごとの予算を取得
    *
    * - カテゴリ詳細に紐づかない予算は空の予算を作成する
    *
    * @param userId ユーザーID
    * @param yyyyMM 予算月
    * @return List[BudgetResponse]
    */
  def search(userId: Id[User], yyyyMM: Int): Future[List[BudgetSummary]] = {
    val (from, to) = fromTo(yyyyMM)
    val condition = BudgetSearchCondition(userId = userId, from = Option(from), to = Option(to))
    for {
      budgets <- repository.search(condition)
      conditionOfIncomeSpending = IncomeSpendingSearchCondition(userId, Option(from), Option(to))
      results <- incomeSpendingRepository.aggregatePerCategoryDetail(conditionOfIncomeSpending)
    } yield makeResponse(budgets, results, from)
  }

  /**
    * 予算・予算詳細を更新する。未設定の場合は登録
    *
    * @param userId ユーザーID
    * @param categoryDetailId カテゴリ詳細ID
    * @param budgetMonth 予算月
    * @param amount 金額
    * @param howToPayId 支払い方法ID
    * @param content 内容
    * @return MutationResponse
    */
  def update(
    userId: Id[User],
    categoryDetailId: Id[CategoryDetail],
    budgetMonth: LocalDate,
    amount: Int,
    howToPayId: Int,
    content: Option[String]
  ): Future[MutationResponse] =
    DB futureLocalTx { implicit session =>
      val entity = Budget(userId, categoryDetailId, budgetMonth, content)

      for {
        originalBudgetOpt <- repository.resolveByCategoryDetailId(
          entity.userId,
          entity.categoryDetailId,
          entity.budgetMonth,
          howToPayId
        )
        newBudget <- updateBudget(originalBudgetOpt, entity)
        _ <- updateBudgetDetail(newBudget, userId, amount, howToPayId)
      } yield MutationResponse(newBudget.budgetId)
    }

  /**
    * 予算月のfrom(月初)とto(月末)を取得する
    *
    * @param yyyyMM 予算月
    * @return (LocalDate, LocalDate)
    */
  private def fromTo(yyyyMM: Int): (LocalDate, LocalDate) = {
    val year = yyyyMM.toString.take(4).toInt
    val month = yyyyMM.toString.takeRight(2).toInt
    val from = LocalDate.of(year, month, 1)
    val to = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
    (from, to)
  }

  /**
    * 予算の関連情報をまとめたBudgetSummaryを作成
    *
    *   - 予算検索のレスポンスで使用する
    *
    * @param budgets List[Budget]
    * @param results　List[IncomeSpendingSummary]
    * @param from LocalDate
    * @return List[BudgetResponse]
    */
  private def makeResponse(
    budgets: List[Budget],
    results: List[IncomeSpendingSummary],
    from: LocalDate
  ): List[BudgetSummary] = {
    val categories = masterCache.allCategories
    val categoryDetails = masterCache.allCategoryDetails

    // 設定済みの予算を設定
    val responseOfBudget = budgets.map(budget => {
      val categoryDetail = categoryDetails
        .find(_.categoryDetailId == budget.categoryDetailId)
        .ensuring(_.isDefined, "[newBudget] category is not found")
        .get
      val result = results.find(_.categoryDetailId == categoryDetail.categoryDetailId)
      BudgetSummary
        .fromEntity(Option(budget), result, categories, categoryDetail, from)
    })

    // 未設定の予算に紐づくカテゴリの予算を作成
    val responses: List[BudgetSummary] = categoryDetails
      .filterNot(c => budgets.exists(_.categoryDetailId == c.categoryDetailId))
      .map(categoryDetail => {
        val result = results.find(_.categoryDetailId == categoryDetail.categoryDetailId)
        BudgetSummary
          .fromEntity(None, result, categories, categoryDetail, from)
      })
    List(responseOfBudget, responses).flatten
  }

  /**
    * すでに予算が登録済みの場合は更新、そうでない場合は登録する
    *
    * @param originalBudgetOpt 設定済み予算（optional）
    * @param entity 登録する予算
    * @return Future[Budget]
    */
  private def updateBudget(
    originalBudgetOpt: Option[Budget],
    entity: Budget
  ): Future[Budget] =
    originalBudgetOpt match {
      case Some(_budget) =>
        val budget = _budget.copy(content = entity.content)
        repository.updateContent(budget)
      case None =>
        repository.register(entity)
    }

  /**
    * すでに予算の詳細が登録済みの場合は更新、そうでない場合は登録する
    *
    * @param newBudget 変更後予算
    * @param amount 金額
    * @param howToPayId 支払い方法ID
    * @param userId ユーザーID
    * @return Future[BudgetDetail]
    */
  private def updateBudgetDetail(
    newBudget: Budget,
    userId: Id[User],
    amount: Int,
    howToPayId: Int
  ): Future[BudgetDetail] =
    if (newBudget.details.isEmpty) {
      val detail = BudgetDetail(newBudget, amount, howToPayId)
      detailRepository.register(detail)
    } else {
      val detail =
        newBudget.details.head
          .copy(amount = amount, howToPayId = Option(howToPayId))
      detailRepository.updateAmount(detail)
    }
}
