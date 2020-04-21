package entities

import java.time.ZonedDateTime
import java.time.temporal.ChronoField

import play.api.data.FormError
import play.api.data.format.{Formats, Formatter}
import play.api.libs.json._

case class Id[T](value: Long)

object Id {
  def apply[T](): Id[T] = {
    val random = ZonedDateTime.now().getLong(ChronoField.NANO_OF_DAY)
    Id(random)
  }

  def apply[T](value: Long): Id[T] = new Id(value)

  //noinspection ConvertExpressionToSAM
  implicit def write[T](implicit fmt: OWrites[T]): OWrites[Id[T]] = new OWrites[Id[T]]() {
    def writes(id: Id[T]): JsObject = Json.obj("value" -> id.value)
  }

  implicit def idFormFormat[T]: Formatter[Id[T]] = new Formatter[Id[T]] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Id[T]] =
      Formats.longFormat.bind(key, data).right.map(Id[T])

    def unbind(key: String, id: Id[T]): Map[String, String] = Map(key -> id.value.toString)
  }
}
