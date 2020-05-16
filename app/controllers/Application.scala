package controllers

import javax.inject.{Inject, Singleton}
import modules.AuthAction
import play.api.http.Writeable
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import graphql.{AuthMiddleware, ContainerImpl, SchemaDefinition}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class Application @Inject()(
  cc: ControllerComponents,
  container: ContainerImpl,
  implicit val ec: ExecutionContext
) extends AbstractController(cc) {

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]
    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) ⇒ Some(parseVariables(vars))
      case obj: JsObject ⇒ Some(obj)
      case _ ⇒ None
    }
    val tokenOpt = request.headers.get("Authorization")

    executeQuery(query, variables, operation, tokenOpt)
  }

  private def parseVariables(variables: String): JsObject =
    if (variables.trim == "" || variables.trim == "null") Json.obj()
    else Json.parse(variables).as[JsObject]

  private def executeQuery(
    query: String,
    variables: Option[JsObject],
    operation: Option[String],
    tokenOpt: Option[String]
  ): Future[Result] =
    // query parsed successfully, time to execute it!
    QueryParser.parse(query) match {
      case Success(queryAst) =>
        Executor
          .execute(
            SchemaDefinition.KakeiboooSchema,
            queryAst,
            container,
            operationName = operation,
            variables = variables getOrElse Json.obj(),
            exceptionHandler = exceptionHandler,
            middleware = AuthMiddleware(tokenOpt) :: Nil
          )
          .map(ok(_))
          .recover {
            case error: QueryAnalysisError ⇒
              error.printStackTrace()
              BadRequest(error.resolveError)
            case error: ErrorWithResolver ⇒
              error.printStackTrace()
              InternalServerError(error.resolveError)
          }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        error.printStackTrace()
        Future.successful(
          BadRequest(
            Json.obj(
              "syntaxError" → error.getMessage,
              "locations" → Json.arr(
                Json.obj(
                  "line" → error.originalError.position.line,
                  "column" → error.originalError.position.column
                )
              )
            )
          )
        )

      case Failure(_) ⇒
        Future(badRequest(JsNull))
    }

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error @ TooComplexQueryError) ⇒
      error.printStackTrace()
      HandledException(error.getMessage)
    case (_, error @ MaxQueryDepthReachedError(_)) ⇒
      error.printStackTrace()
      HandledException(error.getMessage)
    case (_, error) =>
      error.printStackTrace()
      HandledException(error.getMessage)
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")

  def ok[A](s: A)(implicit writeAble: Writeable[A]): Result =
    Ok(s).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Accept" -> "application/json",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, OPTIONS, PATCH, DELETE",
      "Access-Control-Allow-Headers" -> "Origin, Authorization, Accept, Content-Type",
      "Content-Type" -> "application/json"
    )

  def badRequest[A](s: A)(implicit writeAble: Writeable[A]): Result =
    BadRequest(s).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Accept" -> "application/json",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, OPTIONS, PATCH, DELETE",
      "Access-Control-Allow-Headers" -> "Origin, Authorization, Accept, Content-Type",
      "Content-Type" -> "application/json"
    )
}
