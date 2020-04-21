package modules

import caches.MasterCacheImpl
import com.google.inject.AbstractModule

class Modules extends AbstractModule {
  override def configure(): Unit =
    bind(classOf[MasterCache]).to(classOf[MasterCacheImpl]).asEagerSingleton()
}
