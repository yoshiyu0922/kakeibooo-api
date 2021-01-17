package infrastracture.graphql

import infrastracture.graphql.schema.Authorised
import sangria.execution.{
  BeforeFieldResult,
  Middleware,
  MiddlewareBeforeField,
  MiddlewareQueryContext
}
import sangria.schema.Context

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Middleware in graph
  *
  *   - クエリの実行前後で実行させたい処理を入れる
  *
  *   - Authorisedタグを設定しているクエリに対してはtokenOptで認証を行う
  *
  * @param tokenOpt token in request header
  */
case class AuthMiddleware(tokenOpt: Option[String])
    extends Middleware[Container]
    with MiddlewareBeforeField[Container] {
  type QueryVal = Unit
  type FieldVal = Unit

  def beforeQuery(context: MiddlewareQueryContext[Container, _, _]): Unit = ()
  def afterQuery(queryVal: QueryVal, context: MiddlewareQueryContext[Container, _, _]): Unit = ()

  def beforeField(
    queryVal: QueryVal,
    mctx: MiddlewareQueryContext[Container, _, _],
    ctx: Context[Container, _]
  ): BeforeFieldResult[Container, Unit] = {
    val requireAuth = ctx.field.tags contains Authorised
    val securityCtx = ctx.ctx

    if (requireAuth) {
      val userOpt = Await.result(securityCtx.resolveUserByToken(tokenOpt), Duration.Inf)
      if (userOpt.isEmpty) throw new RuntimeException("Authorization by token failed")
    }

    continue
  }
}
