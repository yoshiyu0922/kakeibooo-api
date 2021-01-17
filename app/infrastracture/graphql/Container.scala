package infrastracture.graphql

import core.caches.MasterCacheImpl
import domain.User
import adapter.repositories.{AccountRepository, AssetRepository}
import usecase.{BudgetService, IncomeSpendingService, UserService}

import scala.concurrent.Future

/**
  * query/mutationで実行するクラスを定義
  */
trait Container {
  def masterCache: MasterCacheImpl

  def auth: UserService

  def accountRepo: AccountRepository

  def assetRepo: AssetRepository

  def budgetService: BudgetService

  def incomeSpendingService: IncomeSpendingService

  def resolveUserByToken(token: Option[String]): Future[Option[User]]
}
