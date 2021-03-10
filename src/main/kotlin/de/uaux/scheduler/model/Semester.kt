package de.uaux.scheduler.model

inline class Semester constructor(val code: Int) {
    constructor(type: Type, year: Int) : this(year * 10 + type.code)

    override fun toString(): String {
        val typeInt = code % 10

        check(typeInt == 1 || typeInt == 2)

        val type = Type.values()[typeInt - 1]
        val year = code / 10
        return "$type $year${if (type == Type.WS) "/${year + 1}" else ""}"
    }

    enum class Type {
        SS, WS;

        val code = ordinal + 1
    }
}