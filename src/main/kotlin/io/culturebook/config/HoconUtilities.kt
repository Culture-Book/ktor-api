package io.culturebook.config

import io.culturebook.di.Singletons

fun AppConfig.getProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getString() ?: ""

fun AppConfig.getListProperty() = Singletons.appConfig.propertyOrNull(propertyKey)?.getList() ?: listOf()
