package adapter.repositories

import domain._
import javax.inject.{Inject, Singleton}
import adapter.repositories.ScalikeJDBCUtils._
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
      deletedAt = rs.zonedDateTimeOpt(as.deletedAt),
      accounts = Nil
    )
}

@Singleton
class AssetRepository @Inject()()(implicit val ec: ExecutionContext)
    extends SQLSyntaxSupport[Asset] {
  private val as = AssetRepository.defaultAlias
  private val ac = AccountRepository.defaultAlias

  /**
    * userIdに紐づくAssetを取得
    *
    * @param userId ユーザーID
    * @param s DBSession
    * @return List[Asset]
    */
  def resolveByUserId(userId: Id[User])(implicit s: DBSession = autoSession): Future[List[Asset]] =
    Future {
      withSQL[Asset] {
        select
          .from(AssetRepository as as)
          .leftJoin(AccountRepository as ac)
          .on(as.assetId, ac.assetId)
          .where
          .eq(as.userId, userId.value)
          .orderBy(as.sortIndex)
      }.one(AssetRepository(as))
        .toMany(AccountRepository.opt(ac))
        .map({ (asset, accounts) =>
          asset.copy(accounts = accounts.toList)
        })
        .list
        .apply()
    }
}
