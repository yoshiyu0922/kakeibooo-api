package services

import java.time.LocalDate

import dto.requests.BudgetRequest
import dto.response.budget.{CreateOrUpdateResponse, ListResponse}
import entities.{Budget, BudgetDetail, Id, User}
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import repositories.{BudgetDetailRepository, BudgetRepository, IncomeSpendingRepository}
import scalikejdbc.DB

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BudgetService @Inject()(
  val repository: BudgetRepository,
  val detailRepository: BudgetDetailRepository,
  val incomeSpendingRepository: IncomeSpendingRepository,
  val masterCache: MasterCache
)(
  implicit ec: ExecutionContext
) {
  def list(userId: Id[User], yyyyMM: Int): Future[ListResponse] = {
    val (from, to) = fromTo(yyyyMM)

    for {
      budgets <- repository.findListByDateFromTo(userId, from, to)
      results <- incomeSpendingRepository.findSpendingListGroupByCategory(userId, from, to)
      parentCategories = masterCache.allParentCategories
      categories = masterCache.allCategories
    } yield ListResponse.fromEntities(budgets, results, parentCategories, categories, from)
  }

  private def fromTo(yyyyMM: Int): (LocalDate, LocalDate) = {
    val year = yyyyMM.toString.take(4).toInt
    val month = yyyyMM.toString.takeRight(2).toInt
    val from = LocalDate.of(year, month, 1)
    val to = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
    (from, to)
  }

  def registerOrUpdate(
    userId: Id[User],
    form: BudgetRequest
  ): Future[CreateOrUpdateResponse] =
    DB futureLocalTx { implicit session =>
      val entity = form.convertBudgetEntity(userId)

      for {
        originalBudgetOpt <- repository.resolveByCategoryId(
          entity.categoryId,
          entity.userId,
          entity.budgetMonth,
          form.howToPayId
        )
        newBudget <- registerOrUpdateBudget(originalBudgetOpt, entity)
        _ <- registerOrUpdateBudgetDetail(newBudget, form, userId)
      } yield CreateOrUpdateResponse.fromEntity(newBudget)
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
    * @param budget 予算
    * @param form リクエスト
    * @param userId ユーザーID
    * @return Future[BudgetDetail]
    */
  private def registerOrUpdateBudgetDetail(
    budget: Budget,
    form: BudgetRequest,
    userId: Id[User]
  ): Future[BudgetDetail] =
    if (budget.details.isEmpty) {
      val detail = form.convertToBudgetDetail(budget.budgetId, userId)
      detailRepository.register(detail)
    } else {
      val detail =
        budget.details.head.copy(amount = form.amount, howToPayId = Option(form.howToPayId))
      detailRepository.updateAmount(detail)
    }
}
