package services

import java.time.LocalDate

import dto.response.{IncomeSpendingListResponse, MutationResponse}
import entities._
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import repositories.{
  AccountRepository,
  AccountSearchCondition,
  IncomeSpendingRepository,
  IncomeSpendingSearchCondition
}
import scalikejdbc.DB

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSpendingService @Inject()(
  val repository: IncomeSpendingRepository,
  val masterCache: MasterCache,
  val accountRepository: AccountRepository,
  val accountService: AccountService
)(
  implicit ec: ExecutionContext
) {

  /**
    * 収支を検索する
    *
    * @param userId ユーザーID
    * @param yyyyMMOpt 予算月(optional)
    * @param limitOpt 上限件数(optional)
    * @return IncomeSpending
    */
  def search(
    userId: Id[User],
    yyyyMMOpt: Option[Int],
    limitOpt: Option[Int]
  ): Future[List[IncomeSpendingListResponse]] = {
    val condition = yyyyMMOpt match {
      case Some(yyyyMM) =>
        val year = yyyyMM.toString.take(4).toInt
        val month = yyyyMM.toString.takeRight(2).toInt
        val from = LocalDate.of(year, month, 1)
        val to = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
        IncomeSpendingSearchCondition(userId, Option(from), Option(to))
      case None =>
        IncomeSpendingSearchCondition(userId, None, None)
    }
    repository
      .search(searchCondition = condition, limitOpt = limitOpt)
      .map(_.map(IncomeSpendingListResponse(_, masterCache)))
  }

  /**
    * 収支を登録もしくは更新をする
    *
    *   - incomeSpendingIdOptが "設定: 更新, 未設定: 登録"
    *
    * @param incomeSpendingIdOpt 収支ID(optional)
    * @param userId ユーザーID
    * @param accountId　口座ID
    * @param accrualDate 発生日
    * @param categoryDetailId カテゴリ詳細ID
    * @param amount 金額
    * @param howToPayId 支払い方法ID
    * @param isIncome true: 収入, false: 支出
    * @param content 内容
    * @return MutationResponse
    */
  def execute(
    incomeSpendingIdOpt: Option[Id[IncomeSpending]],
    userId: Id[User],
    accountId: Id[Account],
    accrualDate: LocalDate,
    categoryDetailId: Id[CategoryDetail],
    amount: Int,
    howToPayId: Option[Int],
    isIncome: Boolean,
    content: Option[String]
  ): Future[MutationResponse] = {
    val entity = IncomeSpending(
      incomeSpendingIdOpt,
      userId,
      accountId,
      accrualDate,
      categoryDetailId,
      amount,
      howToPayId,
      isIncome,
      content
    )
    incomeSpendingIdOpt match {
      case Some(_) => update(entity)
      case None    => register(entity)
    }
  }

  /**
    * 支出を削除する
    *
    *   - 削除に伴い口座残高を更新する
    *
    * @param userId ユーザーID
    * @param id 支出ID
    * @return MutationResponse
    */
  def delete(userId: Id[User], id: Id[IncomeSpending]): Future[MutationResponse] =
    for {
      incomeSpending <- repository.resolve(userId, id)
      _ <- repository.deleteData(userId, id)
      balance = accountService.calcBalance(
        incomeSpending,
        incomeSpending.account.balance,
        isRevert = true
      )
      _ <- accountRepository.updateBalance(incomeSpending.accountId, balance)
    } yield MutationResponse(id)

  /**
    * 支出を登録する
    *
    *   - 削除に伴い口座残高を更新する
    *
    * @param newEntity IncomeSpending
    * @return MutationResponse
    */
  private def register(newEntity: IncomeSpending): Future[MutationResponse] =
    DB futureLocalTx { implicit s =>
      for {
        data <- repository.register(newEntity)(s)
        condition = AccountSearchCondition(newEntity.userId, Option(data.accountId))
        account <- accountRepository.resolve(condition)
        _ <- accountService.updateAccount(
          newEntity.userId,
          account.accountId,
          newEntity.incomeSpendingId
        )(s)
      } yield MutationResponse(data.incomeSpendingId.value)
    }

  /**
    * 支出を更新する
    *
    * @param entity IncomeSpending
    * @return MutationResponse
    */
  private def update(entity: IncomeSpending): Future[MutationResponse] =
    DB futureLocalTx { implicit s =>
      for {
        _ <- accountService.revertAccount(entity.userId, entity.incomeSpendingId)(s)
        _ <- repository.updateData(entity)(s)
        condition = AccountSearchCondition(entity.userId, Option(entity.accountId))
        account <- accountRepository.resolve(condition)
        _ <- accountService.updateAccount(
          entity.userId,
          account.accountId,
          entity.incomeSpendingId
        )(s)
      } yield MutationResponse(entity.incomeSpendingId)
    }
}
