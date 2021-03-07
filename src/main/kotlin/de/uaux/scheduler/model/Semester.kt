package de.uaux.scheduler.model

inline class Semester /*private*/ constructor(val code: Int) {
    constructor(type: Type, year: Int) : this(year * 10 + type.code)

    enum class Type {
        SS, WS;

        val code = ordinal + 1
    }
}