package services

import dto.response.AssetResponse
import entities.{Id, User}
import javax.inject.{Inject, Singleton}
import repositories.{AccountRepository, AssetRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AssetService @Inject()(
  val assetRepository: AssetRepository,
  val accountRepository: AccountRepository
)(
  implicit ec: ExecutionContext
) {

  def list(userId: Id[User]): Future[List[AssetResponse]] =
    for {
      assets <- assetRepository.findByUserId(userId)
      accounts <- accountRepository.findByUserId(userId)
    } yield AssetResponse(assets, accounts)
}
