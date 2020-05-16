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

object Budget {
  def apply(
    userId: Id[User],
    categoryDetailId: Id[CategoryDetail],
    budgetMonth: LocalDate,
    content: Option[String]
  ): Budget =
    Budget(
      budgetId = Id[Budget](),
      userId = userId,
      categoryDetailId = categoryDetailId,
      budgetMonth = budgetMonth,
      content = content.getOrElse(""),
      details = Nil,
      createdAt = None,
      updatedAt = None,
      isDeleted = false,
      deletedAt = None
    )
}
