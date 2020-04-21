package entities

import java.time.ZonedDateTime

case class Category(
  categoryId: Id[Category],
  parentCategoryId: Id[ParentCategory],
  name: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
