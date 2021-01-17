package usecase

import core.codemaster.HowToPay
import domain.{Account, Id, IncomeSpending, User}
import javax.inject.{Inject, Singleton}
import adapter.repositories.{AccountRepository, IncomeSpendingRepository}
import scalikejdbc.DBSession

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccountService @Inject()(
  incomeSpendingRepository: IncomeSpendingRepository,
  accountRepository: AccountRepository
)(
  implicit ec: ExecutionContext
) {

  /**
    * 支出IDに紐づく金額分、口座残高を取り消す
    *
    * @param userId ユーザーID
    * @param incomeSpendingId 支出ID
    * @param s DBSession
    * @return Unit
    */
  def revertAccount(
    userId: Id[User],
    incomeSpendingId: Id[IncomeSpending]
  )(
    implicit s: DBSession
  ): Future[Unit] =
    for {
      original <- incomeSpendingRepository.resolve(userId, incomeSpendingId)
      _ <- this.updateAccount(
        userId = userId,
        accountId = original.accountId,
        incomeSpendingId = original.incomeSpendingId,
        isRevert = true
      )
    } yield ()

  /**
    * 口座を更新する
    *
    * @param userId ユーザーID
    * @param accountId 口座ID
    * @param incomeSpendingId 支出ID
    * @param isRevert trueの場合は支出金額分を取り消す
    * @param s DBSession
    * @return Unit
    */
  def updateAccount(
    userId: Id[User],
    accountId: Id[Account],
    incomeSpendingId: Id[IncomeSpending],
    isRevert: Boolean = false
  )(
    implicit s: DBSession
  ): Future[Unit] =
    for {
      original <- incomeSpendingRepository.resolve(userId, incomeSpendingId)
      balance = calcBalance(
        original,
        original.account.balance,
        isRevert
      )
      _ <- accountRepository.updateBalance(accountId, balance)
    } yield ()

  /**
    * 残高を計算する
    * @param incomeSpending 支出
    * @param originalBalance 元残高
    * @param isRevert trueの場合は支出金額分を取り消す
    * @return 残高
    */
  def calcBalance(
    incomeSpending: IncomeSpending,
    originalBalance: Int,
    isRevert: Boolean = false
  ): Int = {
    val revertVariable = if (isRevert) -1 else 1
    val addBalance =
      if (incomeSpending.howToPayId.contains(HowToPay.Cache.id))
        -1 * incomeSpending.amount
      else if (incomeSpending.isIncome)
        incomeSpending.amount
      else
        0

    originalBalance + (revertVariable * addBalance)
  }
}
