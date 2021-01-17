package domain

import java.time.ZonedDateTime

case class CategoryDetail(
  categoryDetailId: Id[CategoryDetail],
  categoryId: Id[Category],
  name: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
