package adapter.repositories

import domain.{Id, User}
import javax.inject.{Inject, Singleton}
import adapter.repositories.ScalikeJDBCUtils._
import scalikejdbc._

object UserRepository extends SQLSyntaxSupport[User] {
  override val tableName = "users"
  private val defaultAlias = syntax("u")

  def apply(s: SyntaxProvider[User])(rs: WrappedResultSet): User =
    apply(s.resultName)(rs)

  def apply(u: ResultName[User])(rs: WrappedResultSet): User =
    User(
      userId = rs.toId[User](u.userId),
      frontUserId = rs.string(u.frontUserId),
      name = rs.string(u.name),
      password = rs.string(u.password),
      createdAt = rs.zonedDateTime(u.createdAt),
      updatedAt = rs.zonedDateTime(u.updatedAt),
      isDeleted = rs.boolean(u.isDeleted),
      deletedAt = rs.zonedDateTimeOpt(u.deletedAt)
    )

}
@Singleton
class UserRepository @Inject()() extends SQLSyntaxSupport[User] {

  private val u = UserRepository.defaultAlias

  /**
    * ユーザIDとパスワードに紐づくUserを取得
    *
    * @param frontUserId ユーザーID（表示用）
    * @param password パスワード
    * @param s DBSession
    * @return Option[User]
    */
  def auth(
    frontUserId: String,
    password: String
  )(implicit s: DBSession = autoSession): Option[User] =
    withSQL {
      select(
        u.result.userId,
        u.result.frontUserId,
        u.result.password,
        u.result.name,
        u.result.createdAt,
        u.result.updatedAt,
        u.result.isDeleted,
        u.result.deletedAt
      ).from(UserRepository as u)
        .where
        .eq(u.frontUserId, frontUserId)
    }.map(UserRepository(u)).single.apply()

  /**
    * ユーザIDに紐づくUserを取得
    *
    * @param userId ユーザーID
    * @param s DBSession
    * @return Option[User]
    */
  def resolveById(userId: Id[User])(implicit s: DBSession = autoSession): Option[User] =
    withSQL {
      select(
        u.result.userId,
        u.result.frontUserId,
        u.result.password,
        u.result.name,
        u.result.createdAt,
        u.result.updatedAt,
        u.result.isDeleted,
        u.result.deletedAt
      ).from(UserRepository as u)
        .where
        .eq(u.userId, userId.value)
    }.map(UserRepository(u)).single.apply()
}
