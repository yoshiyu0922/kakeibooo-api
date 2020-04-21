package dto.response.incomeSpending

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.{Format, Json}

case class IncomeSpendResponse(
  incomeSpendingId: Long,
  userId: Long,
  accountId: Long,
  accrualDate: LocalDate,
  parentCategoryId: Long,
  categoryId: Long,
  accountName: String,
  parentCategoryName: String,
  categoryName: String,
  amount: Int,
  howToPayId: Option[Int],
  howToPayName: String,
  isIncome: Boolean,
  content: String,
  isDeleted: Boolean,
  createdAt: LocalDateTime
)

object IncomeSpendResponse {
  implicit val format: Format[IncomeSpendResponse] = Json.format
}
