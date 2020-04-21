package caches

import com.google.inject._
import entities._
import javax.inject.Singleton
import modules.MasterCache
import repositories._
import scalikejdbc.config.DBs

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class MasterCacheImpl @Inject()(
  val parentCategoryRepository: ParentCategoryRepository,
  val categoryRepository: CategoryRepository
) extends MasterCache {
  private var parentCategories: List[ParentCategory] = Nil
  private var categories: List[Category] = Nil

  override def initialize(): Unit = {
    DBs.setupAll()
    import scala.concurrent.ExecutionContext.Implicits.global

    val process = (for {
      pc <- parentCategoryRepository.findAll()
      c <- categoryRepository.findAll()
    } yield {
      parentCategories = pc
      categories = c
    }).recover {
      case e: Throwable => throw e;
    }
    Await.ready(process, Duration.Inf)
    DBs.closeAll()
  }

  override def allParentCategories: List[ParentCategory] = parentCategories

  override def allCategories: List[Category] = categories

  override def findCategoriesByParentCategoryId(
    parentCategoryId: Id[ParentCategory]
  ): List[Category] =
    categories.filter(_.parentCategoryId == parentCategoryId)

  initialize()
}
