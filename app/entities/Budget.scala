package entities

import java.time.{LocalDate, ZonedDateTime}

case class Budget(
  budgetId: Id[Budget],
  userId: Id[User],
  categoryDetailId: Id[CategoryDetail],
  budgetMonth: LocalDate,
  content: String,
  details: List[BudgetDetail],
  createdAt: Option[ZonedDateTime],
  updatedAt: Option[ZonedDateTime],
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
