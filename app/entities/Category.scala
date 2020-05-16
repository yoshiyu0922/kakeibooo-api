package entities

import java.time.ZonedDateTime

case class Category(
  categoryId: Id[Category],
  name: String,
  isIncome: Boolean,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
