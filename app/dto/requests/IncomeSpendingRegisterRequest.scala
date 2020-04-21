package dto.requests

import java.time.{LocalDate, ZonedDateTime}

import entities.Id._
import entities._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

case class IncomeSpendingRegisterRequest(
  accountId: Id[Account],
  accrualDate: LocalDate,
  categoryId: Id[Category],
  amount: Int,
  howToPayId: Option[Int],
  isIncome: Boolean,
  content: String
) {
  def convertEntity(userId: Id[User]) = IncomeSpending(
    incomeSpendingId = Id[IncomeSpending](),
    userId = userId,
    accountId = accountId,
    accrualDate = accrualDate,
    categoryId = categoryId,
    amount = amount,
    howToPayId = howToPayId,
    isIncome = isIncome,
    content = content,
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now(),
    isDeleted = false,
    deletedAt = None
  )
}

object IncomeSpendingRegisterRequest {

  def mappingForm[T]()(implicit request: Request[T]): Form[IncomeSpendingRegisterRequest] =
    Form(
      mapping(
        "accountId" -> of[Id[Account]],
        "accrualDate" -> localDate,
        "categoryId" -> of[Id[Category]],
        "amount" -> number,
        "howToPayId" -> optional(number),
        "isIncome" -> boolean,
        "content" -> text
      )(IncomeSpendingRegisterRequest.apply)(IncomeSpendingRegisterRequest.unapply)
    ).bindFromRequest()
}
