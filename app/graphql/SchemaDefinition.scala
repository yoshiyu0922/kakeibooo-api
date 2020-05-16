package graphql

import graphql.schema.{MutationType, QueryType}
import sangria.schema._

object SchemaDefinition extends QueryType with MutationType {

  val KakeiboooSchema = Schema(query = Query, mutation = Some(Mutation))
}
