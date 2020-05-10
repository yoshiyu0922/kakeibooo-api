package dto.requests

import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

import entities._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

case class IncomeSpendingUpdateRequest(
  incomeSpendingId: Id[IncomeSpending],
  accountId: Id[Account],
  accrualDate: LocalDate,
  categoryId: Id[Category],
  categoryDetailId: Id[CategoryDetail],
  amount: Int,
  howToPayId: Option[Int],
  isIncome: Boolean,
  content: String,
  createdAt: LocalDateTime
) {
  def convertEntity(userId: Id[User]): IncomeSpending = IncomeSpending(
    incomeSpendingId = incomeSpendingId,
    userId = userId,
    accountId = accountId,
    accrualDate = accrualDate,
    categoryDetailId = categoryDetailId,
    amount = amount,
    howToPayId = howToPayId,
    isIncome = isIncome,
    content = content,
    createdAt = createdAt.atZone(ZoneId.systemDefault()),
    updatedAt = ZonedDateTime.now(),
    isDeleted = false,
    deletedAt = None
  )
}

object IncomeSpendingUpdateRequest {

  def mappingForm[T]()(
    implicit request: Request[T]
  ): Form[IncomeSpendingUpdateRequest] =
    Form(
      mapping(
        "incomeSpendingId" -> of[Id[IncomeSpending]],
        "accountId" -> of[Id[Account]],
        "accrualDate" -> localDate,
        "categoryId" -> of[Id[Category]],
        "categoryDetailId" -> of[Id[CategoryDetail]],
        "amount" -> number,
        "howToPayId" -> optional(number),
        "isIncome" -> boolean,
        "content" -> text,
        "createdAt" -> localDateTime
      )(IncomeSpendingUpdateRequest.apply)(IncomeSpendingUpdateRequest.unapply)
    ).bindFromRequest()

}
