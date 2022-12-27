package sig.g.modules.authentication.logic


fun String?.isProperEmail() = Regex("^([a-zA-Z0-9_\\-]+)@([a-zA-Z0-9_\\-]+)\\.([a-zA-Z]{2,5})\$").matches(this ?: "")

val tosDateRegex =
    Regex("(January|February|March|April|May|June|July|August|September|October|November|December) \\d{1,2}, \\d{4}")