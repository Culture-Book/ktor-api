package uk.co.culturebook.modules.database

import org.jetbrains.exposed.sql.Transaction
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