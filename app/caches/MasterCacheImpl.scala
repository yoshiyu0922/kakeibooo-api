package caches

import entities.{Category, CategoryDetail}
import javax.inject.Inject
import modules.MasterCache

class MasterCacheImpl @Inject()(masterCache: MasterCache) {

  def findAllCategories: List[Category] = masterCache.allCategories

  def findAllCategoryDetails: List[CategoryDetail] =
    masterCache.allCategoryDetails

  def howToPays = HowToPay.list
}
