package adapter.repositories

import domain._
import javax.inject.{Inject, Singleton}
import adapter.repositories.ScalikeJDBCUtils._
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

  def opt(
    s: SyntaxProvider[Account]
  )(rs: WrappedResultSet): Option[Account] =
    rs.toIdOpt[Account](s.resultName.accountId)
      .map(_ => AccountRepository(s)(rs))
}

@Singleton
class AccountRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Account] {
  private val ac = AccountRepository.defaultAlias

  /**
    * 検索条件に該当するAccountを取得
    *
    * @param searchCondition 検索条件
    * @param s DBSession
    * @return Account
    */
  def resolve(searchCondition: AccountSearchCondition)(
    implicit s: DBSession = autoSession
  ): Future[Account] =
    this.search(searchCondition).flatMap {
      case list if list.size == 1 => Future.successful(list.head)
      case _                      => Future.failed(new RuntimeException("duplicated account_id "))
    }

  /**
    * 検索条件にヒットする口座一覧を取得
    *
    * @param searchCondition 検索条件
    * @param s DBSession
    * @return List[Account]
    */
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

  /**
    * where条件を作成する（AND条件）
    *
    * @param searchCondition 検索条件
    * @tparam A typeパラメータ
    * @return Option[SQLSyntax]
    */
  private def makeAndCondition[A](
    searchCondition: AccountSearchCondition
  ): Option[SQLSyntax] =
    sqls.toAndConditionOpt(
      Some(sqls.eq(ac.userId, searchCondition.userId.value)),
      searchCondition.accountId.map(a => sqls.eq(ac.accountId, a.value))
    )

  /**
    * 残高を更新する
    *
    * @param accountId 口座ID
    * @param balance 更新後残高
    * @param s DBSession
    * @return 更新件数
    */
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
