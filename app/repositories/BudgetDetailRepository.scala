package repositories

import entities.{Budget, BudgetDetail, User}
import javax.inject.Inject
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object BudgetDetailRepository extends SQLSyntaxSupport[BudgetDetail] {
  override val tableName = "budget_details"
  val defaultAlias = syntax("bgtd")

  def apply(s: SyntaxProvider[BudgetDetail])(rs: WrappedResultSet): BudgetDetail =
    apply(s.resultName)(rs)

  def apply(bgtd: ResultName[BudgetDetail])(rs: WrappedResultSet): BudgetDetail =
    BudgetDetail(
      budgetDetailId = rs.toId[BudgetDetail](bgtd.budgetDetailId),
      budgetId = rs.toId[Budget](bgtd.budgetId),
      userId = rs.toId[User](bgtd.userId),
      amount = rs.int(bgtd.amount),
      howToPayId = rs.intOpt(bgtd.howToPayId),
      createdAt = rs.zonedDateTimeOpt(bgtd.createdAt),
      updatedAt = rs.zonedDateTimeOpt(bgtd.updatedAt),
      isDeleted = rs.boolean(bgtd.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(bgtd.deletedAt)
    )

  def opt(s: SyntaxProvider[BudgetDetail])(rs: WrappedResultSet): Option[BudgetDetail] =
    rs.toIdOpt[BudgetDetail](s.resultName.budgetDetailId).map(_ => BudgetDetailRepository(s)(rs))

}
class BudgetDetailRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[BudgetDetail] {
  private val bgtd = BudgetDetailRepository.defaultAlias

  def register(entity: BudgetDetail)(implicit s: DBSession = autoSession): Future[BudgetDetail] =
    Future {
      val c = BudgetDetailRepository.column
      withSQL {
        insert
          .into(BudgetDetailRepository)
          .namedValues(
            c.budgetDetailId -> entity.budgetDetailId.value,
            c.budgetId -> entity.budgetId.value,
            c.userId -> entity.userId.value,
            c.amount -> entity.amount,
            c.howToPayId -> entity.howToPayId,
            c.createdAt -> sqls.currentTimestamp,
            c.isDeleted -> false,
            c.deletedAt -> None
          )
      }.update.apply()
      entity
    }

  def updateAmount(entity: BudgetDetail)(
    implicit s: DBSession = autoSession
  ): Future[BudgetDetail] = Future {
    val c = BudgetDetailRepository.column
    withSQL {
      update(BudgetDetailRepository)
        .set(
          c.amount -> entity.amount,
          c.updatedAt -> sqls.currentTimestamp
        )
        .where
        .eq(c.budgetDetailId, entity.budgetDetailId.value)
    }.update.apply()
    entity
  }
}
