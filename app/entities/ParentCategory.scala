package entities

import java.time.ZonedDateTime

case class ParentCategory(
  parentCategoryId: Id[ParentCategory],
  name: String,
  isIncome: Boolean,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
