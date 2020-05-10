package modules

import entities._

trait MasterCache {

  def initialize(): Unit

  def allCategories: List[Category]

  def allCategoryDetails: List[CategoryDetail]

  def findDetailsByCategoryId(categoryId: Id[Category]): List[CategoryDetail]
}
