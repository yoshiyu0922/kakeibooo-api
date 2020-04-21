package modules

import entities._

trait MasterCache {

  def initialize(): Unit

  def allParentCategories: List[ParentCategory]

  def allCategories: List[Category]

  def findCategoriesByParentCategoryId(parentCategoryId: Id[ParentCategory]): List[Category]
}
