package repositories

import entities.ParentCategory
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object ParentCategoryRepository extends SQLSyntaxSupport[ParentCategory] {
  override val tableName = "parent_categories"
  private val defaultAlias = syntax("pc")

  def apply(s: SyntaxProvider[ParentCategory])(rs: WrappedResultSet): ParentCategory =
    apply(s.resultName)(rs)

  def apply(pc: ResultName[ParentCategory])(rs: WrappedResultSet): ParentCategory =
    ParentCategory(
      parentCategoryId = rs.toId[ParentCategory](pc.parentCategoryId),
      name = rs.string(pc.name),
      isIncome = rs.boolean(pc.isIncome),
      createdAt = rs.zonedDateTime(pc.createdAt),
      updatedAt = rs.zonedDateTime(pc.updatedAt),
      isDeleted = rs.boolean(pc.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(pc.deletedAt)
    )
}

@Singleton
class ParentCategoryRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[ParentCategory] {
  private val pc = ParentCategoryRepository.defaultAlias

  def findAll()(implicit s: DBSession = autoSession): Future[List[ParentCategory]] = Future {
    withSQL {
      select(
        pc.result.parentCategoryId,
        pc.result.name,
        pc.result.isIncome,
        pc.result.createdAt,
        pc.result.updatedAt,
        pc.result.isDeleted,
        pc.result.deletedAt
      ).from(ParentCategoryRepository as pc)
    }.map(ParentCategoryRepository(pc)).list.apply()
  }
}
