package uk.co.culturebook.config

import uk.co.culturebook.di.Singletons

fun AppConfig.getProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getString() ?: ""

fun AppConfig.getListProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getList() ?: listOf()
