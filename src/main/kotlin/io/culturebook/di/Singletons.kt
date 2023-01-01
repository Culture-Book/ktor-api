package io.culturebook.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object Singletons {
    val appConfig by lazy {
        if (System.getProperty("test").toBoolean()) {
            HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
        } else {
            HoconApplicationConfig(ConfigFactory.load())
        }
    }

}