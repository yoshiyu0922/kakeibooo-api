package core.caches

import com.google.inject._
import domain._
import javax.inject.Singleton
import core.modules.MasterCache
import adapter.repositories._
import scalikejdbc.config.DBs

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class MasterCacheModule @Inject()(
  val categoryRepository: CategoryRepository,
  val categoryDetailRepository: CategoryDetailRepository
) extends MasterCache {
  private var parentCategories: List[Category] = Nil
  private var categories: List[CategoryDetail] = Nil

  override def initialize(): Unit = {
    DBs.setupAll()
    import scala.concurrent.ExecutionContext.Implicits.global

    val process = (for {
      c <- categoryRepository.findAll()
      cd <- categoryDetailRepository.findAll()
    } yield {
      parentCategories = c
      categories = cd
    }).recover {
      case e: Throwable => throw e;
    }
    Await.ready(process, Duration.Inf)
    DBs.closeAll()
  }

  override def allCategories: List[Category] = parentCategories

  override def allCategoryDetails: List[CategoryDetail] = categories

  override def findDetailsByCategoryId(
    categoryId: Id[Category]
  ): List[CategoryDetail] =
    categories.filter(_.categoryId == categoryId)

  initialize()
}
