package repositories

import java.time.LocalDate

import entities.{Budget, CategoryDetail, Id, User}
import javax.inject.Inject
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object BudgetRepository extends SQLSyntaxSupport[Budget] {
  override val tableName = "budget"
  private val defaultAlias = syntax("bgt")

  def apply(s: SyntaxProvider[Budget])(rs: WrappedResultSet): Budget =
    apply(s.resultName)(rs)

  def apply(bgt: ResultName[Budget])(rs: WrappedResultSet): Budget =
    Budget(
      budgetId = rs.toId[Budget](bgt.budgetId),
      userId = rs.toId[User](bgt.userId),
      categoryDetailId = rs.toId[CategoryDetail](bgt.categoryDetailId),
      budgetMonth = rs.localDate(bgt.budgetMonth),
      content = rs.string(bgt.content),
      details = Nil,
      createdAt = rs.zonedDateTimeOpt(bgt.createdAt),
      updatedAt = rs.zonedDateTimeOpt(bgt.updatedAt),
      isDeleted = rs.boolean(bgt.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(bgt.deletedAt)
    )
}

class BudgetRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Budget] {
  private val bgt = BudgetRepository.defaultAlias
  private val bgtd = BudgetDetailRepository.defaultAlias

  def resolveByCategoryId(
    categoryDetailId: Id[CategoryDetail],
    userId: Id[User],
    budgetMonth: LocalDate,
    howToPayId: Int
  )(implicit s: DBSession = autoSession): Future[Option[Budget]] =
    Future {
      withSQL[Budget] {
        select
          .from(BudgetRepository as bgt)
          .leftJoin(BudgetDetailRepository as bgtd)
          .on(
            sqls
              .eq(bgt.budgetId, bgtd.budgetId)
              .and
              .eq(bgtd.howToPayId, howToPayId)
          )
          .where
          .eq(bgt.userId, userId.value)
          .and
          .eq(bgt.budgetMonth, budgetMonth)
          .and
          .eq(bgt.categoryDetailId, categoryDetailId.value)
      }.one(BudgetRepository(bgt))
        .toMany(BudgetDetailRepository.opt(bgtd))
        .map({ (budget, budgetDetails) =>
          budget.copy(details = budgetDetails.toList)
        })
        .single()
        .apply()
    }

  def findListByDateFromTo(userId: Id[User], from: LocalDate, to: LocalDate)(
    implicit s: DBSession = autoSession
  ): Future[List[Budget]] =
    Future {
      withSQL[Budget] {
        select
          .from(BudgetRepository as bgt)
          .leftJoin(BudgetDetailRepository as bgtd)
          .on(bgt.budgetId, bgtd.budgetId)
          .where
          .eq(bgt.userId, userId.value)
          .and
          .ge(bgt.budgetMonth, from)
          .and
          .le(bgt.budgetMonth, to)
          .orderBy(bgt.categoryDetailId)
          .asc
      }.one(BudgetRepository(bgt))
        .toMany(BudgetDetailRepository.opt(bgtd))
        .map({ (budget, budgetDetails) =>
          budget.copy(details = budgetDetails.toList)
        })
        .list
        .apply()
    }

  def register(
    entity: Budget
  )(implicit s: DBSession = autoSession): Future[Budget] =
    Future {
      val c = BudgetRepository.column
      withSQL {
        insert
          .into(BudgetRepository)
          .namedValues(
            c.budgetId -> entity.budgetId.value,
            c.userId -> entity.userId.value,
            c.categoryDetailId -> entity.categoryDetailId.value,
            c.budgetMonth -> entity.budgetMonth,
            c.content -> entity.content,
            c.createdAt -> sqls.currentTimestamp,
            c.isDeleted -> false,
            c.deletedAt -> None
          )
      }.update.apply()
      entity
    }

  def updateContent(
    entity: Budget
  )(implicit s: DBSession = autoSession): Future[Budget] = Future {
    val c = BudgetRepository.column
    withSQL {
      update(BudgetRepository)
        .set(c.content -> entity.content, c.updatedAt -> sqls.currentTimestamp)
        .where
        .eq(c.budgetId, entity.budgetId.value)
    }.update.apply()
    entity
  }

}
