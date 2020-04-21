package repositories

import entities.Id
import scalikejdbc.WrappedResultSet

object ScalikeJDBCUtils {

  implicit class ConvertTypeToId(self: WrappedResultSet) {
    def toId[T](name: String): Id[T] = Id[T](self.long(name))

    def toIdOpt[T](name: String): Option[Id[T]] = self.longOpt(name).map(Id[T])
  }
}
