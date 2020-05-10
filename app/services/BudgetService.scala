package services

import java.time.LocalDate

import dto.IncomeSpendingSummary
import dto.response.{BudgetResponse, MutationResponse}
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
  def search(userId: Id[User], yyyyMM: Int): Future[List[BudgetResponse]] = {
    val (from, to) = fromTo(yyyyMM)
    val condition = BudgetSearchCondition(userId = userId, from = Option(from), to = Option(to))
    for {
      budgets <- repository.search(condition)
      conditionOfIncomeSpending = IncomeSpendingSearchCondition(userId, Option(from), Option(to))
      results <- incomeSpendingRepository.findSummaryPerCategoryDetail(conditionOfIncomeSpending)
    } yield makeResponse(budgets, results, from)
  }

  private def fromTo(yyyyMM: Int): (LocalDate, LocalDate) = {
    val year = yyyyMM.toString.take(4).toInt
    val month = yyyyMM.toString.takeRight(2).toInt
    val from = LocalDate.of(year, month, 1)
    val to = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
    (from, to)
  }

  private def makeResponse(
    budgets: List[Budget],
    results: List[IncomeSpendingSummary],
    from: LocalDate
  ): List[BudgetResponse] = {
    val categories = masterCache.allCategories
    val categoryDetails = masterCache.allCategoryDetails

    // 設定済みの予算を設定
    val responseOfBudget = budgets.map(budget => {
      val categoryDetail = categoryDetails
        .find(_.categoryDetailId == budget.categoryDetailId)
        .ensuring(_.isDefined, "[newBudget] category is not found")
        .get
      val result = results.find(_.categoryDetailId == categoryDetail.categoryDetailId)
      BudgetResponse
        .fromEntity(Option(budget), result, categories, categoryDetail, from)
    })

    // 未設定の予算に紐づくカテゴリの予算を作成
    val responses: List[BudgetResponse] = categoryDetails
      .filterNot(c => budgets.exists(_.categoryDetailId == c.categoryDetailId))
      .map(categoryDetail => {
        val result = results.find(_.categoryDetailId == categoryDetail.categoryDetailId)
        BudgetResponse
          .fromEntity(None, result, categories, categoryDetail, from)
      })
    List(responseOfBudget, responses).flatten
  }

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
        originalBudgetOpt <- repository.findByCategoryId(
          entity.userId,
          entity.categoryDetailId,
          entity.budgetMonth,
          howToPayId
        )
        newBudget <- registerOrUpdateBudget(originalBudgetOpt, entity)
        _ <- registerOrUpdateBudgetDetail(newBudget, userId, amount, howToPayId)
      } yield MutationResponse(newBudget.budgetId)
    }

  /**
    * すでに予算が登録済みの場合は更新、そうでない場合は登録する
    *
    * @param originalBudgetOpt 設定済み予算（optional）
    * @param entity 登録する予算
    * @return Future[Budget]
    */
  private def registerOrUpdateBudget(
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
  private def registerOrUpdateBudgetDetail(
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
