package services

import java.time.LocalDate

import caches.HowToPay
import dto.response.MutationResponse
import entities._
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import repositories.{
  AccountRepository,
  AccountSearchCondition,
  IncomeSpendingRepository,
  IncomeSpendingSearchCondition
}
import scalikejdbc.{DB, DBSession}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSpendingService @Inject()(
  val repository: IncomeSpendingRepository,
  val masterCache: MasterCache,
  val accountRepository: AccountRepository
)(
  implicit ec: ExecutionContext
) {
  def search(
    userId: Id[User],
    yyyyMMOpt: Option[Int],
    limitOpt: Option[Int]
  ): Future[List[IncomeSpending]] = {
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
    repository.search(searchCondition = condition, limitOpt = limitOpt)
  }

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

  private def register(newEntity: IncomeSpending): Future[MutationResponse] =
    DB futureLocalTx { implicit s =>
      for {
        data <- repository.register(newEntity)(s)
        condition = AccountSearchCondition(newEntity.userId, Option(data.accountId))
        account <- accountRepository.findByAccountId(condition)
        balance = calcBalance(newEntity, account)
        _ <- accountRepository.updateBalance(data.accountId, balance)
      } yield MutationResponse(data.incomeSpendingId.value)
    }

  def update(entity: IncomeSpending): Future[MutationResponse] =
    DB futureLocalTx { implicit s =>
      for {
        _ <- revertIncomeSpendingBefore(entity.userId, entity)(s)
        _ <- repository.updateData(entity)(s)
        _ <- updateAccount(entity.userId, entity)(s)
      } yield MutationResponse(entity.incomeSpendingId)
    }

  def delete(userId: Id[User], id: Id[IncomeSpending]): Future[MutationResponse] =
    for {
      incomeSpending <- repository.resolveUnique(userId, id)
      _ <- repository.deleteData(userId, id)
      account = incomeSpending.account
        .ensuring(_.isDefined, "account is empty in IncomeSpendService.delete")
        .get
      balance = calcBalance(incomeSpending, account, isRevert = true)
      _ <- accountRepository.updateBalance(incomeSpending.accountId, balance)
    } yield MutationResponse(id)

  private def calcBalance(
    data: IncomeSpending,
    account: Account,
    isRevert: Boolean = false
  ): Int = {
    val revertVariable = if (isRevert) -1 else 1
    val addBalance =
      if (data.howToPayId.contains(HowToPay.Cache.id))
        -1 * data.amount
      else if (data.isIncome)
        data.amount
      else
        0

    account.balance + (revertVariable * addBalance)
  }

  private def revertIncomeSpendingBefore(userId: Id[User], data: IncomeSpending)(
    implicit s: DBSession
  ) =
    for {
      beforeData <- repository.resolveUnique(userId, data.incomeSpendingId)
      account = beforeData.account
        .ensuring(_.isDefined, "account is empty in IncomeSpendService.delete")
        .get
      revertBalance = calcBalance(beforeData, account, isRevert = true)
      _ <- accountRepository.updateBalance(beforeData.accountId, revertBalance)
    } yield ()

  private def updateAccount(userId: Id[User], entity: IncomeSpending)(implicit s: DBSession) = {
    val condition = AccountSearchCondition(userId, Option(entity.accountId))
    for {
      account <- accountRepository.findByAccountId(condition)
      balance = calcBalance(entity, account)
      _ <- accountRepository.updateBalance(entity.accountId, balance)
    } yield ()
  }
}
