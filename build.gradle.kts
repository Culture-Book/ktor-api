import java.io.FileInputStream
import java.util.*

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

val exposedVersion: String = "0.40.1"
val hikariVersion: String = "5.0.1"
val postgresVersion: String = "42.6.0"
val bouncyCastleVersion: String = "1.72.3"
val firebaseVersion: String = "9.1.1"
val h2Version: String = "2.1.214"

val localProperties = Properties()
try {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
} catch (ignored: Exception) {
}

tasks.withType<JavaExec> {
    localProperties["DB_PORT"]?.let { environment("DB_PORT", it) }
    localProperties["EMAIL_ACCOUNT"]?.let { environment("EMAIL_ACCOUNT", it) }
    localProperties["EMAIL_HOST"]?.let { environment("EMAIL_HOST", it) }
    localProperties["EMAIL_PASSWORD"]?.let { environment("EMAIL_PASSWORD", it) }
    localProperties["SMTP_PORT"]?.let { environment("SMTP_PORT", it) }
    localProperties["SUPABASE_API_KEY"]?.let { environment("SUPABASE_API_KEY", it) }
    localProperties["SUPABASE_TOKEN"]?.let { environment("SUPABASE_TOKEN", it) }
}

plugins {
    application
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("io.ktor.plugin") version "2.1.2"
}

group = "uk.co.culturebook"
version = "0.0.1"

application {
    mainClass.set("uk.co.culturebook.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {

    // Client Deps
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    // Database dependencies
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")

    // Authentication dependencies
    implementation("org.bouncycastle:bcpg-jdk15to18:$bouncyCastleVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")

    // Serialization & Content negotiation
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")

    // Logging and Monitoring
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // HTTP
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")

    // Email
    implementation("org.apache.commons:commons-email:1.5")

    // Websockets and sessions
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")

    // Ktor core and engines
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-locations-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // Test implementations
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}