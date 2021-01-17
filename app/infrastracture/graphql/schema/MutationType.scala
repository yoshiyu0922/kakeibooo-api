package infrastracture.graphql.schema

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import adapter.dto.response.{AuthTokenResponse, MutationResponse}
import domain._
import infrastracture.graphql.Container
import sangria.schema._

/**
  * mutationのスキーマ定義
  */
trait MutationType extends ArgType {
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  lazy val AuthType = ObjectType(
    name = "Auth",
    description = "ログイン認証",
    fields = fields[Container, AuthTokenResponse](
      Field(
        name = "token",
        fieldType = StringType,
        description = Some("トークン"),
        resolve = _.value.token
      )
    )
  )

  lazy val BudgetUpdateResponseType = ObjectType(
    name = "BudgetUpdateResponse",
    description = "予算更新(レスポンス)",
    fields = fields[Container, MutationResponse](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("予算ID"),
        resolve = _.value.id
      )
    )
  )

  lazy val IncomeSpendingUpdateResponseType = ObjectType(
    name = "IncomeSpendingUpdateResponse",
    description = "予算更新(レスポンス)",
    fields = fields[Container, MutationResponse](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("収支ID"),
        resolve = _.value.id
      )
    )
  )

  lazy val IncomeSpendingDeleteResponseType = ObjectType(
    name = "IncomeSpendingDeleteResponse",
    description = "予算削除(レスポンス)",
    fields = fields[Container, MutationResponse](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("収支ID"),
        resolve = _.value.id
      )
    )
  )

  val Mutation = ObjectType(
    "Mutation",
    fields[Container, Unit](
      Field(
        name = "auth",
        fieldType = AuthType,
        description = Some("IDとパスワードで認証"),
        arguments = FrontUserIdArg :: PasswordArg :: Nil,
        resolve = ctx => ctx.ctx.auth.auth(ctx.arg(FrontUserIdArg), ctx.arg(PasswordArg))
      ),
      Field(
        name = "updateBudget",
        fieldType = BudgetUpdateResponseType,
        description = Some("予算を更新(未設定なら登録)"),
        arguments = UserIdArg :: CategoryDetailIdArg :: BudgetMonthArg :: AmountArg :: HowToPayIdArg :: ContentArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx => {
          val userId = Id[User](ctx.arg(UserIdArg))
          val categoryDetailId = Id[CategoryDetail](ctx.arg(CategoryDetailIdArg))
          val budgetMonth = LocalDate.parse(ctx.arg(BudgetMonthArg), dateFormatter)
          val amount = ctx.arg(AmountArg)
          val howToPayId = ctx.arg(HowToPayIdArg)
          val content = ctx.arg(ContentArg)

          ctx.ctx.budgetService
            .update(userId, categoryDetailId, budgetMonth, amount, howToPayId, content)
        }
      ),
      Field(
        name = "updateIncomeSpending",
        fieldType = IncomeSpendingUpdateResponseType,
        description = Some("収支を更新(未設定なら登録)"),
        arguments = IncomeSpendingIdOptArg ::
          UserIdArg ::
          AccountIdArg ::
          CategoryDetailIdArg ::
          AccrualDateArg ::
          AmountArg ::
          HowToPayIdOptArg ::
          IsIncomeArg ::
          ContentArg ::
          Nil,
        tags = Authorised :: Nil,
        resolve = ctx => {
          val incomeSpendingIdOpt = ctx.arg(IncomeSpendingIdOptArg).map(Id[IncomeSpending])
          val userId = Id[User](ctx.arg(UserIdArg))
          val accountId = Id[Account](ctx.arg(AccountIdArg))
          val categoryDetailId = Id[CategoryDetail](ctx.arg(CategoryDetailIdArg))
          val accrualDate = LocalDate.parse(ctx.arg(AccrualDateArg), dateFormatter)
          val amount = ctx.arg(AmountArg)
          val howToPayId = ctx.arg(HowToPayIdOptArg)
          val isIncome = ctx.arg(IsIncomeArg)
          val content = ctx.arg(ContentArg)

          ctx.ctx.incomeSpendingService
            .execute(
              incomeSpendingIdOpt,
              userId,
              accountId,
              accrualDate,
              categoryDetailId,
              amount,
              howToPayId,
              isIncome,
              content
            )
        }
      ),
      Field(
        name = "deleteIncomeSpending",
        fieldType = IncomeSpendingDeleteResponseType,
        description = Some("収支を削除"),
        arguments = IncomeSpendingIdArg :: UserIdArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx => {
          val incomeSpendingId = Id[IncomeSpending](ctx.arg(IncomeSpendingIdArg))
          val userId = Id[User](ctx.arg(UserIdArg))

          ctx.ctx.incomeSpendingService.delete(userId, incomeSpendingId)
        }
      )
    )
  )
}
