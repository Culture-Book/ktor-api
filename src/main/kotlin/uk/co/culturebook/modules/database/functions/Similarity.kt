package uk.co.culturebook.modules.database.functions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.stringParam

class Similarity(columnA: Column<String>, string: String) :
    CustomFunction<Double>("MY_SIMILARITY", DoubleColumnType(), columnA, stringParam(string))