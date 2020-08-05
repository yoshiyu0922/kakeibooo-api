package graphql.schema

import sangria.schema.{Argument, BooleanType, IntType, LongType, OptionInputType, StringType}

/**
  * graphqlのリクエストパラメータの定義
  */
trait ArgType {

  val TokenArg = Argument(name = "token", argumentType = StringType, description = "token")
  val UserIdArg = Argument(name = "userId", argumentType = LongType, description = "user id")
  val FrontUserIdArg =
    Argument(name = "userId", argumentType = StringType, description = "front user id")
  val PasswordArg = Argument(name = "password", argumentType = StringType, description = "password")
  val BudgetMonthIntArg =
    Argument(name = "yyyymm", argumentType = IntType, description = "予算月（YYYYMM）")
  val ConditionMonthArg =
    Argument(
      name = "yyyymm",
      argumentType = OptionInputType(IntType),
      description = "month(yyyyMM) in serach condition"
    )
  val AccountIdOptArg = Argument(
    name = "accountId",
    argumentType = OptionInputType(LongType),
    description = "account id"
  )
  val limitArg =
    Argument(name = "limit", argumentType = OptionInputType(IntType), description = "検索上限件数")
  val CategoryDetailIdArg =
    Argument(name = "categoryDetailId", argumentType = LongType, description = "カテゴリ詳細ID")
  val BudgetMonthArg =
    Argument(name = "budgetMonth", argumentType = StringType, description = "予算月")
  val AmountArg =
    Argument(name = "amount", argumentType = IntType, description = "金額")
  val HowToPayIdArg =
    Argument(name = "howToPayId", argumentType = IntType, description = "支払い方法ID")
  val HowToPayIdOptArg =
    Argument(name = "howToPayId", argumentType = OptionInputType(IntType), description = "支払い方法ID")
  val ContentArg =
    Argument(name = "content", argumentType = OptionInputType(StringType), description = "内容")
  val AccountIdArg =
    Argument(name = "accountId", argumentType = LongType, description = "口座ID")
  val IsIncomeArg =
    Argument(name = "isIncome", argumentType = BooleanType, description = "true: 収入, false: 支出")
  val AccrualDateArg =
    Argument(name = "accrualDate", argumentType = StringType, description = "支出発生日")
  val IncomeSpendingIdOptArg =
    Argument(
      name = "incomeSpendingId",
      argumentType = OptionInputType(LongType),
      description = "支出ID"
    )
  val IncomeSpendingIdArg =
    Argument(name = "incomeSpendingId", argumentType = LongType, description = "支出ID")

}
