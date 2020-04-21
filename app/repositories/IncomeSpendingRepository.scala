package repositories

import java.time.LocalDate

import dto.{IncomeSpendingDto, IncomeSpendingPerCategoryDto}
import entities._
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object IncomeSpendingRepository extends SQLSyntaxSupport[IncomeSpending] {
  override val tableName = "income_spendings"
  private val defaultAlias = syntax("icsp")

  def apply(s: SyntaxProvider[IncomeSpending])(rs: WrappedResultSet): IncomeSpending =
    apply(s.resultName)(rs)

  def apply(is: ResultName[IncomeSpending])(rs: WrappedResultSet): IncomeSpending =
    IncomeSpending(
      incomeSpendingId = rs.toId[IncomeSpending](is.incomeSpendingId),
      userId = rs.toId[User](is.userId),
      accountId = rs.toId[Account](is.accountId),
      accrualDate = rs.localDate(is.accrualDate),
      categoryId = rs.toId[Category](is.categoryId),
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

  def register(data: IncomeSpending)(implicit s: DBSession): Future[IncomeSpending] = Future {
    val c = IncomeSpendingRepository.column
    withSQL {
      insert
        .into(IncomeSpendingRepository)
        .namedValues(
          c.incomeSpendingId -> data.incomeSpendingId.value,
          c.userId -> data.userId.value,
          c.accountId -> data.accountId.value,
          c.accrualDate -> data.accrualDate,
          c.categoryId -> data.categoryId.value,
          c.amount -> data.amount,
          c.howToPayId -> data.howToPayId,
          c.isIncome -> data.isIncome,
          c.content -> data.content,
          c.createdAt -> sqls.currentTimestamp,
          c.updatedAt -> sqls.currentTimestamp,
          c.isDeleted -> false
        )
    }.update.apply()
    data
  }

  def resolveByUserId(
    userId: Id[User],
    limitOpt: Option[Int]
  )(implicit s: DBSession = autoSession): Future[List[IncomeSpendingDto]] = Future {
    withSQL {
      val limit = limitOpt.getOrElse(Int.MaxValue)
      select
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where
        .eq(is.isDeleted, false)
        .limit(limit)
    }.map(rs => {
        val incomeSpending = IncomeSpendingRepository(is)(rs)
        val account = AccountRepository(ac)(rs)
        IncomeSpendingDto(incomeSpending, account)
      })
      .list
      .apply()
  }

  def resolveUnique(
    userId: Id[User],
    id: Id[IncomeSpending]
  )(implicit s: DBSession = autoSession): Future[IncomeSpendingDto] = Future {
    withSQL {
      select
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where
        .eq(is.isDeleted, false)
        .and
        .eq(is.incomeSpendingId, id.value)
    }.map(rs => {
        val incomeSpending = IncomeSpendingRepository(is)(rs)
        val account = AccountRepository(ac)(rs)
        IncomeSpendingDto(incomeSpending, account)
      })
      .single()
      .apply()
      .ensuring(_.isDefined, s"not found IncomeSpending Data (id: $id})")
      .get
  }

  def findListByDateFromTo(userId: Id[User], from: LocalDate, to: LocalDate, limitOpt: Option[Int])(
    implicit s: DBSession = autoSession
  ): Future[List[IncomeSpendingDto]] = Future {
    withSQL {
      val limit = limitOpt.getOrElse(Int.MaxValue)
      select
        .from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where
        .eq(is.userId, userId.value)
        .and
        .ge(is.accrualDate, from)
        .and
        .le(is.accrualDate, to)
        .orderBy(is.accrualDate)
        .desc
        .limit(limit)
    }.map(rs => {
        val incomeSpending = IncomeSpendingRepository(is)(rs)
        val account = AccountRepository(ac)(rs)
        IncomeSpendingDto(incomeSpending, account)
      })
      .list
      .apply()
  }

  def findSpendingListGroupByCategory(userId: Id[User], from: LocalDate, to: LocalDate)(
    implicit s: DBSession = autoSession
  ): Future[List[IncomeSpendingPerCategoryDto]] = Future {
    withSQL {
      select(
        is.userId,
        is.categoryId,
        is.howToPayId,
        sqls.sum(is.amount)
      ).from(IncomeSpendingRepository as is)
        .innerJoin(AccountRepository as ac)
        .on(is.accountId, ac.accountId)
        .where
        .eq(is.userId, userId.value)
        .and
        .ge(is.accrualDate, from)
        .and
        .le(is.accrualDate, to)
        .and
        .isNotNull(is.howToPayId)
        .groupBy(is.userId, is.categoryId, is.howToPayId)
    }.map(rs => {
        IncomeSpendingPerCategoryDto(
          rs.toId[User](is.userId),
          rs.toId[Category](is.categoryId),
          rs.int(is.howToPayId),
          rs.int(4)
        )
      })
      .list
      .apply()
  }

  def deleteData(userId: Id[User], id: Id[IncomeSpending])(
    implicit s: DBSession = autoSession
  ): Future[Int] =
    Future {
      val c = IncomeSpendingRepository.column
      withSQL {
        delete
          .from(IncomeSpendingRepository)
          .where
          .eq(c.incomeSpendingId, id.value)
          .and
          .eq(c.userId, userId.value)
      }.update().apply()
    }

  def updateData(data: IncomeSpending)(implicit s: DBSession): Future[Int] =
    Future {
      val c = IncomeSpendingRepository.column
      withSQL {
        update(IncomeSpendingRepository)
          .set(
            c.accountId -> data.accountId.value,
            c.accrualDate -> data.accrualDate,
            c.categoryId -> data.categoryId.value,
            c.amount -> data.amount,
            c.howToPayId -> data.howToPayId,
            c.isIncome -> data.isIncome,
            c.content -> data.content,
            c.updatedAt -> sqls.currentTimestamp
          )
          .where
          .eq(c.incomeSpendingId, data.incomeSpendingId.value)
      }.update().apply()
    }
}
