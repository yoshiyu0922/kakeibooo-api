package services

import dto.response.AccountResponse
import entities.{Id, User}
import javax.inject.{Inject, Singleton}
import repositories.{AccountRepository, AssetRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccountService @Inject()(
  val assetRepository: AssetRepository,
  val accountRepository: AccountRepository
)(
  implicit ec: ExecutionContext
) {

  def list(userId: Id[User]): Future[List[AccountResponse]] =
    accountRepository.findByUserId(userId).map(l => AccountResponse(l))
}
