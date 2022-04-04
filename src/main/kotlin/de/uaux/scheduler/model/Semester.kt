package de.uaux.scheduler.model

@JvmInline
value class Semester constructor(val code: Int) {
    constructor(type: Type, year: Int) : this(year * TYPE_CODE_RADIX + type.code)

    override fun toString(): String {
        val typeInt = code % TYPE_CODE_RADIX

        check(typeInt == 1 || typeInt == 2)

        val type = Type.values()[typeInt - 1]
        val year = code / TYPE_CODE_RADIX
        return "$type $year${if (type == Type.WS) "/${year + 1}" else ""}"
    }

    enum class Type {
        SS, WS;

        val code = ordinal + 1
    }

    private companion object {
        /**
         * Used to pack both year and semester type into a single Int
         */
        private const val TYPE_CODE_RADIX = 10
    }
}