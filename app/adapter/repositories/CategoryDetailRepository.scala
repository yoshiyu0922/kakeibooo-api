package adapter.repositories

import domain.{Category, CategoryDetail}
import javax.inject.{Inject, Singleton}
import adapter.repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object CategoryDetailRepository extends SQLSyntaxSupport[CategoryDetail] {
  override val tableName = "category_detail"
  private val defaultAlias = syntax("c")

  def apply(
    s: SyntaxProvider[CategoryDetail]
  )(rs: WrappedResultSet): CategoryDetail =
    apply(s.resultName)(rs)

  def apply(
    pc: ResultName[CategoryDetail]
  )(rs: WrappedResultSet): CategoryDetail =
    CategoryDetail(
      categoryDetailId = rs.toId[CategoryDetail](pc.categoryDetailId),
      categoryId = rs.toId[Category](pc.categoryId),
      name = rs.string(pc.name),
      createdAt = rs.zonedDateTime(pc.createdAt),
      updatedAt = rs.zonedDateTime(pc.updatedAt),
      isDeleted = rs.boolean(pc.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(pc.deletedAt)
    )
}

@Singleton
class CategoryDetailRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[CategoryDetail] {
  private val c = CategoryDetailRepository.defaultAlias

  /**
    * カテゴリ詳細を全て取得する
    * @param s DBSession
    * @return List[CategoryDetail]
    */
  def findAll()(
    implicit s: DBSession = autoSession
  ): Future[List[CategoryDetail]] =
    Future {
      withSQL {
        select(
          c.result.categoryDetailId,
          c.result.categoryId,
          c.result.name,
          c.result.createdAt,
          c.result.updatedAt,
          c.result.isDeleted,
          c.result.deletedAt
        ).from(CategoryDetailRepository as c)
      }.map(CategoryDetailRepository(c)).list.apply()
    }
}
