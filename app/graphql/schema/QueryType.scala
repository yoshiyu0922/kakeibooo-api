package graphql.schema

import java.time.format.DateTimeFormatter

import caches.HowToPay
import dto.IncomeSpendingSummary
import dto.response.BudgetResponse
import entities._
import graphql.Container
import repositories.AccountSearchCondition
import sangria.schema._

trait QueryType extends ArgType {
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  lazy val CategoryType = ObjectType(
    name = "Category",
    description = "カテゴリ",
    fields = fields[Container, Category](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("カテゴリID"),
        resolve = _.value.categoryId.value
      ),
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("カテゴリ名"),
        resolve = _.value.name
      ),
      Field(
        name = "isIncome",
        fieldType = BooleanType,
        description = Some("true: 収入カテゴリ, false: 支出カテゴリ"),
        resolve = _.value.isIncome
      ),
      Field(
        name = "createdAt",
        fieldType = StringType,
        description = Some("作成日時"),
        resolve = _.value.createdAt.format(dateTimeFormatter)
      ),
      Field(
        name = "update",
        fieldType = StringType,
        description = Some("更新日時"),
        resolve = _.value.updatedAt.format(dateTimeFormatter)
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val CategoryDetailType = ObjectType(
    name = "CategoryDetail",
    description = "カテゴリ詳細",
    fields = fields[Container, CategoryDetail](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("カテゴリ詳細ID"),
        resolve = _.value.categoryDetailId.value
      ),
      Field(
        name = "category_id",
        fieldType = LongType,
        description = Some("カテゴリID"),
        resolve = _.value.categoryId.value
      ),
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("カテゴリ詳細名"),
        resolve = _.value.name
      ),
      Field(
        name = "createdAt",
        fieldType = StringType,
        description = Some("作成日時"),
        resolve = _.value.createdAt.format(dateTimeFormatter)
      ),
      Field(
        name = "update",
        fieldType = StringType,
        description = Some("更新日時"),
        resolve = _.value.updatedAt.format(dateTimeFormatter)
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val HowToPayType = ObjectType(
    name = "HowToPay",
    description = "支払い方法",
    fields = fields[Container, HowToPay](
      Field(
        name = "id",
        fieldType = IntType,
        description = Some("支払い方法ID"),
        resolve = _.value.id
      ),
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("支払い方法名"),
        resolve = _.value.name
      )
    )
  )

  lazy val AccountType = ObjectType(
    name = "Account",
    description = "口座",
    fields = fields[Container, Account](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("口座ID"),
        resolve = _.value.accountId.value
      ),
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "assetId",
        fieldType = LongType,
        description = Some("資産ID"),
        resolve = _.value.assetId.value
      ),
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("口座名"),
        resolve = _.value.name
      ),
      Field(
        name = "balance",
        fieldType = IntType,
        description = Some("口座残高"),
        resolve = _.value.balance
      ),
      Field(
        name = "sortIndex",
        fieldType = IntType,
        description = Some("表示順"),
        resolve = _.value.sortIndex
      ),
      Field(
        name = "createdAt",
        fieldType = StringType,
        description = Some("作成日時"),
        resolve = _.value.createdAt.format(dateTimeFormatter)
      ),
      Field(
        name = "update",
        fieldType = StringType,
        description = Some("更新日時"),
        resolve = _.value.updatedAt.format(dateTimeFormatter)
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val AssetType = ObjectType(
    name = "Asset",
    description = "資産",
    fields = fields[Container, Asset](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("資産ID"),
        resolve = _.value.assetId.value
      ),
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "name",
        fieldType = StringType,
        description = Some("資産名"),
        resolve = _.value.name
      ),
      Field(
        name = "sortIndex",
        fieldType = IntType,
        description = Some("表示順"),
        resolve = _.value.sortIndex
      ),
      Field(
        name = "createdAt",
        fieldType = StringType,
        description = Some("作成日時"),
        resolve = _.value.createdAt.format(dateTimeFormatter)
      ),
      Field(
        name = "update",
        fieldType = StringType,
        description = Some("更新日時"),
        resolve = _.value.updatedAt.format(dateTimeFormatter)
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val BudgetType = ObjectType(
    name = "Budget",
    description = "予算",
    fields = fields[Container, Budget](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("予算ID"),
        resolve = _.value.budgetId.value
      ),
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "categoryDetailId",
        fieldType = LongType,
        description = Some("カテゴリ詳細ID"),
        resolve = _.value.categoryDetailId.value
      ),
      Field(
        name = "budgetMonth",
        fieldType = StringType,
        description = Some("予算年月"),
        resolve = _.value.budgetMonth.format(dateFormatter)
      ),
      Field(
        name = "content",
        fieldType = StringType,
        description = Some("内容"),
        resolve = _.value.content
      ),
      Field(
        name = "budgetDetail",
        fieldType = OptionType(ListType(BudgetDetailType)),
        description = Some("予算詳細"),
        resolve = _.value.details
      ),
      Field(
        name = "createdAt",
        fieldType = OptionType(StringType),
        description = Some("作成日時"),
        resolve = _.value.createdAt.map(_.format(dateTimeFormatter))
      ),
      Field(
        name = "update",
        fieldType = OptionType(StringType),
        description = Some("更新日時"),
        resolve = _.value.updatedAt.map(_.format(dateTimeFormatter))
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val BudgetDetailType = ObjectType(
    name = "BudgetDetail",
    description = "予算詳細",
    fields = fields[Container, BudgetDetail](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("予算詳細ID"),
        resolve = _.value.budgetId.value
      ),
      Field(
        name = "budgetId",
        fieldType = LongType,
        description = Some("予算ID"),
        resolve = _.value.budgetId.value
      ),
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "amount",
        fieldType = IntType,
        description = Some("予算金額"),
        resolve = _.value.amount
      ),
      Field(
        name = "howToPayId",
        fieldType = OptionType(IntType),
        description = Some("支払い方法ID"),
        resolve = _.value.howToPayId
      ),
      Field(
        name = "createdAt",
        fieldType = OptionType(StringType),
        description = Some("作成日時"),
        resolve = _.value.createdAt.map(_.format(dateTimeFormatter))
      ),
      Field(
        name = "update",
        fieldType = OptionType(StringType),
        description = Some("更新日時"),
        resolve = _.value.updatedAt.map(_.format(dateTimeFormatter))
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  lazy val IncomeSpendingSummaryType = ObjectType(
    name = "IncomeSpendingSummary",
    description = "予算月の支出金額の合計",
    fields = fields[Container, IncomeSpendingSummary](
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "categoryDetailId",
        fieldType = LongType,
        description = Some("カテゴリ詳細ID"),
        resolve = _.value.categoryDetailId.value
      ),
      Field(
        name = "howToPayId",
        fieldType = IntType,
        description = Some("支払い方法ID"),
        resolve = _.value.howToPayId
      ),
      Field(
        name = "amount",
        fieldType = IntType,
        description = Some("金額"),
        resolve = _.value.amount
      )
    )
  )

  val BudgetResponseType = ObjectType(
    name = "BudgetResponseType",
    description = "予算（response用）",
    fields = fields[Container, BudgetResponse](
      Field(
        name = "month",
        fieldType = StringType,
        description = Some("予算月"),
        resolve = _.value.month.format(dateFormatter)
      ),
      Field(
        name = "budget",
        fieldType = OptionType(BudgetType),
        description = Some("予算"),
        resolve = _.value.budget
      ),
      Field(
        name = "category",
        fieldType = CategoryType,
        description = Some("カテゴリ"),
        resolve = _.value.category
      ),
      Field(
        name = "categoryDetail",
        fieldType = CategoryDetailType,
        description = Some("カテゴリ詳細"),
        resolve = _.value.categoryDetail
      ),
      Field(
        name = "result",
        fieldType = OptionType(IncomeSpendingSummaryType),
        description = Some("実績"),
        resolve = _.value.result
      )
    )
  )

  lazy val IncomeSpendingType = ObjectType(
    name = "IncomeSpendingType",
    description = "支出",
    fields = fields[Container, IncomeSpending](
      Field(
        name = "id",
        fieldType = LongType,
        description = Some("支出ID"),
        resolve = _.value.incomeSpendingId.value
      ),
      Field(
        name = "userId",
        fieldType = LongType,
        description = Some("ユーザーID"),
        resolve = _.value.userId.value
      ),
      Field(
        name = "accountId",
        fieldType = LongType,
        description = Some("口座ID"),
        resolve = _.value.accountId.value
      ),
      Field(
        name = "categoryDetailId",
        fieldType = LongType,
        description = Some("カテゴリ詳細ID"),
        resolve = _.value.categoryDetailId.value
      ),
      Field(
        name = "amount",
        fieldType = IntType,
        description = Some("金額"),
        resolve = _.value.amount
      ),
      Field(
        name = "howToPayId",
        fieldType = OptionType(IntType),
        description = Some("支払い方法ID"),
        resolve = _.value.howToPayId
      ),
      Field(
        name = "isIncome",
        fieldType = OptionType(IntType),
        description = Some("true: 収入、false: 支出"),
        resolve = _.value.howToPayId
      ),
      Field(
        name = "content",
        fieldType = StringType,
        description = Some("内容"),
        resolve = _.value.content
      ),
      Field(
        name = "createdAt",
        fieldType = OptionType(StringType),
        description = Some("作成日時"),
        resolve = _.value.createdAt.format(dateTimeFormatter)
      ),
      Field(
        name = "update",
        fieldType = OptionType(StringType),
        description = Some("更新日時"),
        resolve = _.value.updatedAt.format(dateTimeFormatter)
      ),
      Field(
        name = "isDeleted",
        fieldType = BooleanType,
        description = Some("true: 削除済み, false: 削除済みでない"),
        resolve = _.value.isDeleted
      ),
      Field(
        name = "deletedAt",
        fieldType = OptionType(StringType),
        description = Some("削除日時"),
        resolve = _.value.deletedAt.map(_.format(dateTimeFormatter))
      )
    )
  )

  val Query = ObjectType(
    name = "Query",
    fields = fields[Container, Unit](
      Field(
        name = "categories",
        fieldType = ListType(CategoryType),
        description = Some("カテゴリを全て取得"),
        tags = Authorised :: Nil,
        resolve = ctx => ctx.ctx.masterCache.findAllCategories
      ),
      Field(
        name = "categoryDetails",
        fieldType = ListType(CategoryDetailType),
        description = Some("カテゴリ詳細を全て取得"),
        tags = Authorised :: Nil,
        resolve = ctx => ctx.ctx.masterCache.findAllCategoryDetails
      ),
      Field(
        name = "howToPay",
        fieldType = ListType(HowToPayType),
        description = Some("支払い方法を全て取得"),
        tags = Authorised :: Nil,
        resolve = ctx => ctx.ctx.masterCache.howToPays
      ),
      Field(
        name = "account",
        fieldType = ListType(AccountType),
        description = Some("指定したユーザの口座を検索"),
        arguments = UserIdArg :: AccountIdOptArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx => {
          val condition = AccountSearchCondition(
            userId = Id[User](ctx.arg(UserIdArg)),
            accountId = ctx.arg(AccountIdOptArg).map(Id[Account])
          )
          ctx.ctx.accountRepo.search(condition)
        }
      ),
      Field(
        name = "asset",
        fieldType = ListType(AssetType),
        description = Some("指定したユーザの資産一覧を取得"),
        arguments = UserIdArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx => ctx.ctx.assetRepo.findByUserId(Id[User](ctx.arg(UserIdArg)))
      ),
      Field(
        name = "budget",
        fieldType = ListType(BudgetResponseType),
        description = Some("指定したユーザの対象年月の予算一覧を取得"),
        arguments = UserIdArg :: BudgetMonthIntArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx =>
          ctx.ctx.budgetService.search(Id[User](ctx.arg(UserIdArg)), ctx.arg(BudgetMonthIntArg))
      ),
      Field(
        name = "searchIncomeSpending",
        fieldType = ListType(IncomeSpendingType),
        description = Some("指定した条件で支出一覧を取得"),
        arguments = UserIdArg :: ConditionMonthArg :: limitArg :: Nil,
        tags = Authorised :: Nil,
        resolve = ctx => {
          val userId = Id[User](ctx.arg(UserIdArg))
          val month = ctx.arg(ConditionMonthArg)
          val limit = ctx.arg(limitArg)
          ctx.ctx.incomeSpendingService.search(userId, month, limit)
        }
      )
    )
  )

}
