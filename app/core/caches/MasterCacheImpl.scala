package core.caches

import core.codemaster.HowToPay
import domain.{Category, CategoryDetail}
import javax.inject.Inject
import core.modules.MasterCache

class MasterCacheImpl @Inject()(masterCache: MasterCache) {

  def findAllCategories: List[Category] = masterCache.allCategories

  def findAllCategoryDetails: List[CategoryDetail] =
    masterCache.allCategoryDetails

  def howToPays = HowToPay.list
}
