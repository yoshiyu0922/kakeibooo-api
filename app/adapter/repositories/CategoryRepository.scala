package adapter.repositories

import domain.Category
import javax.inject.{Inject, Singleton}
import adapter.repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object CategoryRepository extends SQLSyntaxSupport[Category] {
  override val tableName = "category"
  private val defaultAlias = syntax("pc")

  def apply(s: SyntaxProvider[Category])(rs: WrappedResultSet): Category =
    apply(s.resultName)(rs)

  def apply(pc: ResultName[Category])(rs: WrappedResultSet): Category =
    Category(
      categoryId = rs.toId[Category](pc.categoryId),
      name = rs.string(pc.name),
      isIncome = rs.boolean(pc.isIncome),
      createdAt = rs.zonedDateTime(pc.createdAt),
      updatedAt = rs.zonedDateTime(pc.updatedAt),
      isDeleted = rs.boolean(pc.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(pc.deletedAt)
    )
}

@Singleton
class CategoryRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Category] {
  private val pc = CategoryRepository.defaultAlias

  /**
    * カテゴリを全て取得する
    *
    * @param s DBSession
    * @return List[Category]
    */
  def findAll()(implicit s: DBSession = autoSession): Future[List[Category]] =
    Future {
      withSQL {
        select(
          pc.result.categoryId,
          pc.result.name,
          pc.result.isIncome,
          pc.result.createdAt,
          pc.result.updatedAt,
          pc.result.isDeleted,
          pc.result.deletedAt
        ).from(CategoryRepository as pc)
      }.map(CategoryRepository(pc)).list.apply()
    }
}
