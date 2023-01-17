package uk.co.culturebook.modules.database

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.vendors.H2Dialect
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect

internal fun Transaction.getDistanceFunction() {
    when (db.dialect) {
        is PostgreSQLDialect -> exec(
            """
                CREATE OR REPLACE FUNCTION DISTANCE_IN_KM(lat1 DOUBLE PRECISION, lon1 DOUBLE PRECISION, lat2 DOUBLE PRECISION, lon2 DOUBLE PRECISION)
                RETURNS DOUBLE PRECISION AS ${'$'}${'$'}
                    SELECT (6371 * acos(cos(radians(lat1)) * cos(radians(lat2)) * cos(radians(lon2) - radians(lon1)) + sin(radians(lat1)) * sin(radians(lat2)))) AS distance
                ${'$'}${'$'} LANGUAGE SQL;
            """.trimIndent()
        )

        is H2Dialect -> exec(
            """
                CREATE ALIAS IF NOT EXISTS DISTANCE_IN_KM DETERMINISTIC FOR 'uk.co.culturebook.utils.DistanceUtilsKt.getDistanceInKm';
            """.trimIndent()
        )

        else -> exec(
            """
                CREATE FUNCTION IF NOT EXISTS DISTANCE_IN_KM(lat1 DOUBLE PRECISION, lon1 DOUBLE PRECISION, lat2 DOUBLE PRECISION, lon2 DOUBLE PRECISION)
                RETURNS DOUBLE PRECISION AS ${'$'}${'$'}
                    SELECT (6371 * acos(cos(radians(lat1)) * cos(radians(lat2)) * cos(radians(lon2) - radians(lon1)) + sin(radians(lat1)) * sin(radians(lat2)))) AS distance
                ${'$'}${'$'} LANGUAGE SQL;
            """.trimIndent()
        )
    }

}

internal fun Transaction.similarityFunction() {
    when (db.dialect) {
        is PostgreSQLDialect -> exec(
            """
                CREATE EXTENSION IF NOT EXISTS pg_trgm;
                SET pg_trgm.similarity_threshold = 0.8;  
                
                CREATE OR REPLACE FUNCTION MY_SIMILARITY(str1 text, str2 text) RETURNS real AS ${'$'}${'$'}
                SELECT similarity(str1, str2)
                ${'$'}${'$'} LANGUAGE SQL;
            """.trimIndent()
        )

        is H2Dialect -> exec(
            """
                CREATE ALIAS IF NOT EXISTS MY_SIMILARITY DETERMINISTIC FOR 'uk.co.culturebook.utils.SearchUtilsKt.matchStrings';
            """.trimIndent()
        )
    }

}