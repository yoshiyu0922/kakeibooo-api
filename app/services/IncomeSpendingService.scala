package services

import java.time.LocalDate

import caches.HowToPay
import dto.requests.{IncomeSpendingRegisterRequest, IncomeSpendingUpdateRequest}
import dto.response.incomeSpending.{DeleteResponse, ListResponse, RegisterResponse, UpdateResponse}
import entities.{Account, Id, IncomeSpending, User}
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import repositories.{AccountRepository, IncomeSpendingRepository}
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

  def register(userId: Id[User], data: IncomeSpendingRegisterRequest): Future[RegisterResponse] =
    DB futureLocalTx { implicit s =>
      val entity = data.convertEntity(userId)
      for {
        data <- repository.register(entity)
        account <- accountRepository.findByAccountId(userId, data.accountId)
        balance = calcBalance(entity, account)
        _ <- accountRepository.updateBalance(data.accountId, balance)
      } yield RegisterResponse(data.incomeSpendingId.value)
    }

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

  def list(userId: Id[User], limitOpt: Option[Int]): Future[ListResponse] =
    repository
      .resolveByUserId(userId, limitOpt)
      .map(list => {
        ListResponse.fromEntities(list, masterCache)
      })

  def listOfMonth(userId: Id[User], yyyyMM: Int, limitOpt: Option[Int]): Future[ListResponse] = {
    val year = yyyyMM.toString.take(4).toInt
    val month = yyyyMM.toString.takeRight(2).toInt
    val from = LocalDate.of(year, month, 1)
    val to = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
    repository
      .findListByDateFromTo(userId, from, to, limitOpt)
      .map(list => {
        ListResponse.fromEntities(list, masterCache)
      })
  }

  def delete(userId: Id[User], id: Id[IncomeSpending]): Future[DeleteResponse] =
    for {
      data <- repository.resolveUnique(userId, id)
      count <- repository.deleteData(userId, id)
      account <- accountRepository.findByAccountId(userId, data.incomeSpending.accountId)
      balance = calcBalance(data.incomeSpending, account, true)
      _ <- accountRepository.updateBalance(data.incomeSpending.accountId, balance)
    } yield DeleteResponse(id.value, count)

  def update(userId: Id[User], data: IncomeSpendingUpdateRequest): Future[UpdateResponse] =
    DB futureLocalTx { implicit s =>
      val entity = data.convertEntity(userId)
      for {
        _ <- revertIncomeSpendingBefore(userId, entity)
        _ <- repository.updateData(entity)
        _ <- updateAccount(userId, entity)
      } yield UpdateResponse(entity.incomeSpendingId.value)
    }

  private def revertIncomeSpendingBefore(userId: Id[User], data: IncomeSpending)(
    implicit s: DBSession
  ) =
    for {
      beforeData <- repository.resolveUnique(userId, data.incomeSpendingId)
      account <- accountRepository.findByAccountId(userId, beforeData.incomeSpending.accountId)
      revertBalance = calcBalance(beforeData.incomeSpending, account, true)
      _ <- accountRepository.updateBalance(beforeData.incomeSpending.accountId, revertBalance)
    } yield ()

  private def updateAccount(userId: Id[User], entity: IncomeSpending)(implicit s: DBSession) =
    for {
      account <- accountRepository.findByAccountId(userId, entity.accountId)
      balance = calcBalance(entity, account)
      _ <- accountRepository.updateBalance(entity.accountId, balance)
    } yield ()
}
