package dto.response

import entities.Account
import play.api.libs.json.{Format, Json}

case class AccountResponse(
  accountId: Long,
  userId: Long,
  assetId: Long,
  name: String,
  balance: Int,
  sortIndex: Int,
  isDeleted: Boolean
)

object AccountResponse {
  implicit val format: Format[AccountResponse] = Json.format

  def apply(accounts: List[Account]): List[AccountResponse] =
    accounts.map(
      account =>
        AccountResponse(
          accountId = account.accountId.value,
          userId = account.userId.value,
          assetId = account.assetId.value,
          name = account.name,
          balance = account.balance,
          sortIndex = account.sortIndex,
          isDeleted = account.isDeleted
        )
    )

}
