package uk.co.culturebook.modules.database.functions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.doubleParam

class Distance(
    lat1: Column<Double>,
    lon1: Column<Double>,
    lat2: Double,
    lon2: Double
) : CustomFunction<Double>("DISTANCE_IN_KM", DoubleColumnType(), lat1, lon1, doubleParam(lat2), doubleParam(lon2))