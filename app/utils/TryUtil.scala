package utils

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object TryUtil {

  implicit class TryImplicit[A](self: Try[A]) {

    def toFuture: Future[A] = self match {
      case Success(v) => Future.successful(v)
      case Failure(e) => Future.failed(e)
    }
  }
}
