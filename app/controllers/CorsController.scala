package controllers

import javax.inject._
import play.api.i18n.MessagesApi
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CorsController @Inject()(cc: ControllerComponents, messagesApi: MessagesApi)
    extends KakeiboooController(cc, messagesApi) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(path: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    ok("")
  }
}
