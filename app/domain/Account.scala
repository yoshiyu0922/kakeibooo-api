package domain

import java.time.ZonedDateTime

case class Account(
  accountId: Id[Account],
  userId: Id[User],
  assetId: Id[Asset],
  name: String,
  balance: Int,
  sortIndex: Int,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
