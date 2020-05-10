package repositories

import entities._
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

case class AccountSearchCondition(
  userId: Id[User],
  accountId: Option[Id[Account]] = None
)

object AccountRepository extends SQLSyntaxSupport[Account] {
  override val tableName = "account"
  val defaultAlias = syntax("ac")

  def apply(s: SyntaxProvider[Account])(rs: WrappedResultSet): Account =
    apply(s.resultName)(rs)

  def apply(as: ResultName[Account])(rs: WrappedResultSet): Account =
    Account(
      accountId = rs.toId[Account](as.accountId),
      userId = rs.toId[User](as.accountId),
      assetId = rs.toId[Asset](as.assetId),
      name = rs.string(as.name),
      balance = rs.int(as.balance),
      sortIndex = rs.int(as.sortIndex),
      createdAt = rs.zonedDateTime(as.createdAt),
      updatedAt = rs.zonedDateTime(as.updatedAt),
      isDeleted = rs.boolean(as.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(as.deletedAt)
    )
}

@Singleton
class AccountRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Account] {
  private val ac = AccountRepository.defaultAlias

  def search(
    searchCondition: AccountSearchCondition
  )(implicit s: DBSession = autoSession): Future[List[Account]] =
    Future {
      withSQL {
        select(
          ac.result.accountId,
          ac.result.userId,
          ac.result.assetId,
          ac.result.name,
          ac.result.balance,
          ac.result.sortIndex,
          ac.result.createdAt,
          ac.result.updatedAt,
          ac.result.isDeleted,
          ac.result.deletedAt
        ).from(AccountRepository as ac)
          .where(makeAndCondition(searchCondition))
          .orderBy(ac.sortIndex)
      }.map(AccountRepository(ac)).list.apply()
    }

  private def makeAndCondition[A](
    searchCondition: AccountSearchCondition
  ): Option[SQLSyntax] =
    sqls.toAndConditionOpt(
      Some(sqls.eq(ac.userId, searchCondition.userId.value)),
      searchCondition.accountId.map(a => sqls.eq(ac.accountId, a.value))
    )

  def findByAccountId(searchCondition: AccountSearchCondition)(
    implicit s: DBSession = autoSession
  ): Future[Account] =
    this.search(searchCondition).flatMap {
      case list if list.size == 1 => Future.successful(list.head)
      case _                      => Future.failed(new RuntimeException("duplicated account_id "))
    }

  def updateBalance(accountId: Id[Account], balance: Int)(
    implicit s: DBSession = autoSession
  ): Future[Int] = Future {
    val c = AccountRepository.column
    withSQL {
      update(AccountRepository)
        .set(c.balance -> balance)
        .where
        .eq(c.accountId, accountId.value)
    }.update().apply()
  }
}
