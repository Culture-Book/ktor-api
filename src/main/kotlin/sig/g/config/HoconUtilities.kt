package sig.g.config

import sig.g.di.Singletons

fun AppConfig.getProperty() = Singletons.appConfig.property(propertyKey).getString()