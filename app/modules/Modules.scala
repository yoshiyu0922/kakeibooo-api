package modules

import caches.MasterCacheModule
import com.google.inject.AbstractModule

class Modules extends AbstractModule {
  override def configure(): Unit =
    bind(classOf[MasterCache])
      .to(classOf[MasterCacheModule])
      .asEagerSingleton()
}
