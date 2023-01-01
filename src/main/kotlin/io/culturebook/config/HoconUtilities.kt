package io.culturebook.config

import sig.g.di.Singletons

fun _root_ide_package_.io.culturebook.config.AppConfig.getProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getString() ?: ""

fun _root_ide_package_.io.culturebook.config.AppConfig.getListProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getList() ?: listOf()
