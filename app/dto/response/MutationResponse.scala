package dto.response

import entities.Id

case class MutationResponse(
  id: Long
)

object MutationResponse {

  def apply[A](id: Id[A]): MutationResponse =
    new MutationResponse(id.value)
}
