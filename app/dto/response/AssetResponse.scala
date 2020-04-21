package dto.response

import entities.{Account, Asset}
import play.api.libs.json.{Format, Json}

case class AssetResponse(
  assetId: Long,
  name: String,
  accounts: List[AccountResponse],
  sortIndex: Int,
  isDeleted: Boolean
)

object AssetResponse {
  implicit val format: Format[AssetResponse] = Json.format

  def apply(assets: List[Asset], accounts: List[Account]): List[AssetResponse] =
    assets.map(as => {
      val targetAccounts = accounts.filter(_.assetId == as.assetId).sortBy(_.sortIndex)

      AssetResponse(
        assetId = as.assetId.value,
        name = as.name,
        accounts = AccountResponse(targetAccounts),
        sortIndex = as.sortIndex,
        isDeleted = as.isDeleted
      )
    })
}
