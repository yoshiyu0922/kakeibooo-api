package repositories

import java.time.LocalDate

import dto.IncomeSpendingSummary
import entities._
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

case class IncomeSpendingSearchCondition(
  userId: Id[User],
  from: Option[LocalDate],
  to: Option[LocalDate]
)

object IncomeSpendingRepository extends SQLSyntaxSupport[IncomeSpending] {
  override val tableName = "income_spending"
  private val defaultAlias = syntax("icsp")

  def apply(
    s: SyntaxProvider[IncomeSpending]
  )(rs: WrappedResultSet): IncomeSpending =
    apply(s.resultName)(rs)

  def apply(
    is: ResultName[IncomeSpending]
  )(rs: WrappedResultSet): IncomeSpending =
    IncomeSpending(
      incomeSpendingId = rs.toId[IncomeSpending](is.incomeSpendingId),
      userId = rs.toId[User](is.userId),
      accountId = rs.toId[Account](is.accountId),
      accrualDate = rs.localDate(is.accrualDate),
      categoryDetailId = rs.toId[CategoryDetail](is.categoryDetailId),
      amount = rs.int(is.amount),
      howToPayId = rs.intOpt(is.howToPayId),
      isIncome = rs.boolean(is.isIncome),
      content = rs.string(is.content),
      createdAt = rs.zonedDateTime(is.createdAt),
      updatedAt = rs.zonedDateTime(is.updatedAt),
      isDeleted = rs.boolean(is.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(is.deletedAt)
    )
}

@Singleton
class IncomeSpendingRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[IncomeSpending] {
  private val is = IncomeSpendingRepository.defaultAlias
  private val ac = AccountRepository.defaultAlias

  /**
    * userIdとincomeSpendingIdに紐づくIncomeSpendingを取得
    *
    * @param userId　ユーザーID
    * @param id 支出ID
    * @param s DBSession
    * @return IncomeSpending
    */
  def resolve(userId: Id[User], id: Id[IncomeSpending])(
    implicit s: DBSession = autoSession
  ): Future[IncomeSpending] = Future {
    withSQL {
      select
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where
        .eq(is.userId, userId.value)
        .and
        .eq(is.isDeleted, false)
        .and
        .eq(is.incomeSpendingId, id.value)
    }.map(rs => {
        val incomeSpending = IncomeSpendingRepository(is)(rs)
        val account = AccountRepository(ac)(rs)
        incomeSpending.copy(accountOpt = Option(account))
      })
      .single()
      .apply()
      .ensuring(_.isDefined, s"not found IncomeSpending Data (id: $id})")
      .get
  }

  /**
    * 検索条件に紐づくIncomeSpendingの一覧を取得
    *
    * @param searchCondition 検索条件
    * @param limitOpt 上限件数（optional）
    * @param s DBSession
    * @return List[IncomeSpending]
    */
  def search(
    searchCondition: IncomeSpendingSearchCondition,
    limitOpt: Option[Int]
  )(
    implicit s: DBSession = autoSession
  ): Future[List[IncomeSpending]] = Future {
    withSQL {
      val limit = limitOpt.getOrElse(Int.MaxValue)
      select
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where(makeAndCondition(searchCondition))
        .and
        .eq(is.isDeleted, false)
        .limit(limit)
    }.map(rs => {
        val incomeSpending = IncomeSpendingRepository(is)(rs)
        val account = AccountRepository(ac)(rs)
        incomeSpending.copy(accountOpt = Option(account))
      })
      .list
      .apply()
  }

  /**
    * カテゴリ詳細IDに紐づくIncomeSpendingの一覧を取得
    *
    *   - 金額はカテゴリ詳細IDの合計
    *
    * @param searchCondition 検索条件
    * @param s DBSession
    * @return List[IncomeSpendingSummary]
    */
  def aggregatePerCategoryDetail(
    searchCondition: IncomeSpendingSearchCondition
  )(
    implicit s: DBSession = autoSession
  ): Future[List[IncomeSpendingSummary]] = Future {
    withSQL {
      select(is.userId, is.categoryDetailId, is.howToPayId, sqls.sum(is.amount))
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where(makeAndCondition(searchCondition))
        .and
        .isNotNull(is.howToPayId)
        .groupBy(is.userId, is.categoryDetailId, is.howToPayId)
    }.map(rs => {
        IncomeSpendingSummary(
          rs.toId[User](is.userId),
          rs.toId[CategoryDetail](is.categoryDetailId),
          rs.int(is.howToPayId),
          rs.int(4)
        )
      })
      .list
      .apply()
  }

  /**
    * where条件を作成する（AND条件）
    *
    * @param searchCondition 検索条件
    * @tparam A typeパラメータ
    * @return Option[SQLSyntax]
    */
  private def makeAndCondition[A](
    searchCondition: IncomeSpendingSearchCondition
  ): Option[SQLSyntax] =
    sqls.toAndConditionOpt(
      Some(sqls.eq(is.userId, searchCondition.userId.value)),
      searchCondition.from.map(f => sqls.ge(is.accrualDate, f)),
      searchCondition.to.map(t => sqls.le(is.accrualDate, t))
    )

  /**
    * IncomeSpendingの登録をする
    *
    * @param entity IncomeSpending
    * @param s DBSession
    * @return IncomeSpending
    */
  def register(entity: IncomeSpending)(implicit s: DBSession): Future[IncomeSpending] = Future {
    val c = IncomeSpendingRepository.column
    withSQL {
      insert
        .into(IncomeSpendingRepository)
        .namedValues(
          c.incomeSpendingId -> entity.incomeSpendingId.value,
          c.userId -> entity.userId.value,
          c.accountId -> entity.accountId.value,
          c.accrualDate -> entity.accrualDate,
          c.categoryDetailId -> entity.categoryDetailId.value,
          c.amount -> entity.amount,
          c.howToPayId -> entity.howToPayId,
          c.isIncome -> entity.isIncome,
          c.content -> entity.content,
          c.createdAt -> sqls.currentTimestamp,
          c.updatedAt -> sqls.currentTimestamp,
          c.isDeleted -> false
        )
    }.update.apply()
    entity
  }

  /**
    * IncomeSpendingの更新をする
    *
    * @param entity IncomeSpending
    * @param s DBSession
    * @return 更新件数
    */
  def updateData(entity: IncomeSpending)(implicit s: DBSession): Future[Int] =
    Future {
      val c = IncomeSpendingRepository.column
      withSQL {
        update(IncomeSpendingRepository)
          .set(
            c.accountId -> entity.accountId.value,
            c.accrualDate -> entity.accrualDate,
            c.categoryDetailId -> entity.categoryDetailId.value,
            c.amount -> entity.amount,
            c.howToPayId -> entity.howToPayId,
            c.isIncome -> entity.isIncome,
            c.content -> entity.content,
            c.updatedAt -> sqls.currentTimestamp
          )
          .where
          .eq(c.incomeSpendingId, entity.incomeSpendingId.value)
      }.update().apply()
    }

  /**
    * IncomeSpendingの削除をする
    *
    * @param userId ユーザーID
    * @param id 支出ID
    * @param s DBSession
    * @return 削除件数
    */
  def deleteData(userId: Id[User], id: Id[IncomeSpending])(
    implicit s: DBSession = autoSession
  ): Future[Int] =
    Future {
      val c = IncomeSpendingRepository.column
      withSQL {
        update(IncomeSpendingRepository)
          .set(
            c.isDeleted -> true,
            c.deletedAt -> sqls.currentTimestamp
          )
          .where
          .eq(c.incomeSpendingId, id.value)
          .and
          .eq(c.userId, userId.value)
      }.update().apply()
    }
}
