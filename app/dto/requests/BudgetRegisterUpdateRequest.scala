package dto.requests

import java.time.LocalDate

import entities._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

case class BudgetRequest(
  categoryDetailId: Id[CategoryDetail],
  budgetMonth: LocalDate,
  amount: Int,
  howToPayId: Int,
  isIncome: Boolean,
  content: Option[String]
) {
  def convertBudgetEntity(userId: Id[User]): Budget =
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

  def convertToBudgetDetail(budgetId: Id[Budget], userId: Id[User]) =
    BudgetDetail(
      budgetDetailId = Id[BudgetDetail](),
      budgetId = budgetId,
      userId = userId,
      amount = amount,
      howToPayId = Option(howToPayId),
      createdAt = None,
      updatedAt = None,
      isDeleted = false,
      deletedAt = None
    )
}

object BudgetBundleRequest {
  def mappingForm[T]()(implicit request: Request[T]): Form[BudgetRequest] =
    Form(
      mapping(
        "categoryDetailId" -> of[Id[CategoryDetail]],
        "budgetMonth" -> localDate,
        "amount" -> number,
        "howToPayId" -> number,
        "isIncome" -> boolean,
        "content" -> optional(text)
      )(BudgetRequest.apply)(BudgetRequest.unapply)
    ).bindFromRequest()
}
