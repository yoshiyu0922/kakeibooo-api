package graphql

import caches.MasterCacheImpl
import entities.User
import repositories.{AccountRepository, AssetRepository}
import services.{BudgetService, IncomeSpendingService, UserService}

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
