package entities

import java.time.ZonedDateTime

import play.api.libs.json._

case class User(
  userId: Id[User],
  frontUserId: String,
  name: String,
  password: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
) {}

object User {
  implicit val write: OWrites[User] = Json.writes[User]

  implicit val read: Reads[User] = new Reads[User] {
    override def reads(json: JsValue): JsSuccess[User] = {
      val userIdValue = (json \ "userId" \ "value").as[Long]
      val user = User(
        userId = Id[User](userIdValue),
        frontUserId = (json \ "frontUserId").as[String],
        name = (json \ "name").as[String],
        password = (json \ "password").as[String],
        createdAt = (json \ "createdAt").as[ZonedDateTime],
        updatedAt = (json \ "updatedAt").as[ZonedDateTime],
        isDeleted = (json \ "isDeleted").as[Boolean],
        deletedAt = (json \ "deletedAt").asOpt[ZonedDateTime]
      )

      JsSuccess(user)
    }
  }
}
