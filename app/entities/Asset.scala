package entities

import java.time.ZonedDateTime

case class Asset(
  assetId: Id[Asset],
  userId: Id[User],
  name: String,
  sortIndex: Int,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
