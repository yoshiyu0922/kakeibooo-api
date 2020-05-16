package repositories

import entities._
import javax.inject.{Inject, Singleton}
import repositories.ScalikeJDBCUtils._
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

object AssetRepository extends SQLSyntaxSupport[Asset] {
  override val tableName = "asset"
  private val defaultAlias = syntax("ast")

  def apply(s: SyntaxProvider[Asset])(rs: WrappedResultSet): Asset =
    apply(s.resultName)(rs)

  def apply(as: ResultName[Asset])(rs: WrappedResultSet) =
    Asset(
      assetId = rs.toId[Asset](as.assetId),
      userId = rs.toId[User](as.userId),
      name = rs.string(as.name),
      sortIndex = rs.int(as.sortIndex),
      createdAt = rs.zonedDateTime(as.createdAt),
      updatedAt = rs.zonedDateTime(as.updatedAt),
      isDeleted = rs.boolean(as.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(as.deletedAt)
    )
}

@Singleton
class AssetRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Asset] {
  private val as = AssetRepository.defaultAlias

  def findByUserId(
    userId: Id[User]
  )(implicit s: DBSession = autoSession): Future[List[Asset]] =
    Future {
      withSQL {
        select(
          as.result.assetId,
          as.result.userId,
          as.result.name,
          as.result.sortIndex,
          as.result.createdAt,
          as.result.updatedAt,
          as.result.isDeleted,
          as.result.deletedAt
        ).from(AssetRepository as as)
          .where
          .eq(as.userId, userId.value)
          .orderBy(as.sortIndex)
      }.map(AssetRepository(as)).list.apply()
    }
}
