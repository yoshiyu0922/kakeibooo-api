package graphql

import graphql.schema.{MutationType, QueryType}
import sangria.schema._

/**
  * スキーマの定義
  */
object SchemaDefinition extends QueryType with MutationType {

  val KakeiboooSchema = Schema(query = Query, mutation = Some(Mutation))
}
