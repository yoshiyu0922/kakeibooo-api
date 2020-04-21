package repositories

import entities.{Category, ParentCategory}
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object CategoryRepository extends SQLSyntaxSupport[Category] {
  override val tableName = "categories"
  private val defaultAlias = syntax("c")

  def apply(s: SyntaxProvider[Category])(rs: WrappedResultSet): Category =
    apply(s.resultName)(rs)

  def apply(pc: ResultName[Category])(rs: WrappedResultSet): Category =
    Category(
      categoryId = rs.toId[Category](pc.categoryId),
      parentCategoryId = rs.toId[ParentCategory](pc.parentCategoryId),
      name = rs.string(pc.name),
      createdAt = rs.zonedDateTime(pc.createdAt),
      updatedAt = rs.zonedDateTime(pc.updatedAt),
      isDeleted = rs.boolean(pc.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(pc.deletedAt)
    )
}

@Singleton
class CategoryRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Category] {
  private val c = CategoryRepository.defaultAlias

  def findAll()(implicit s: DBSession = autoSession): Future[List[Category]] = Future {
    withSQL {
      select(
        c.result.categoryId,
        c.result.parentCategoryId,
        c.result.name,
        c.result.createdAt,
        c.result.updatedAt,
        c.result.isDeleted,
        c.result.deletedAt
      ).from(CategoryRepository as c)
    }.map(CategoryRepository(c)).list.apply()
  }
}
