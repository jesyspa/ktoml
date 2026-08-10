package com.akuleshov7.ktoml.parsers

import com.akuleshov7.ktoml.exceptions.InternalEncodingException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringUtilsTest {
    @Test
    fun testIsLineEndingBackslash() {
        assertEquals(true, "a\\\nb".isLineEndingBackslash(1))
        assertEquals(true, "a\\  \nb".isLineEndingBackslash(1))
        assertEquals(true, "a\\".isLineEndingBackslash(1))
        assertEquals(true, "a\\  ".isLineEndingBackslash(1))
        assertEquals(false, "a\\b".isLineEndingBackslash(1))
    }

    @Test
    fun testIsLineEndingBackslashRejectsOutOfBoundsIndex() {
        assertFailsWith<InternalEncodingException> {
            "abc".isLineEndingBackslash(3)
        }

        assertFailsWith<InternalEncodingException> {
            "abc".isLineEndingBackslash(10)
        }

        // backslashIndex + 1 is 0 here, a valid position, so the scan would run to completion
        // and return a plausible answer for a backslash that is not in the string.
        assertFailsWith<InternalEncodingException> {
            "abc".isLineEndingBackslash(-1)
        }
    }

    @Test
    fun testForTakeBeforeComment() {
        var lineWithoutComment = "test_key = \"test_value\"# \" some comment".takeBeforeComment(false)
        assertEquals("test_key = \"test_value\"", lineWithoutComment)

        lineWithoutComment = "key = \"\"\"value\"\"\"# \"".takeBeforeComment(false)
        assertEquals("key = \"\"\"value\"\"\"", lineWithoutComment)

        lineWithoutComment = "key = 123# \"\"\"abc".takeBeforeComment(false)
        assertEquals("key = 123", lineWithoutComment)

        lineWithoutComment = "key = \"ab\\\"#cdef\"#123".takeBeforeComment(false)
        assertEquals("key = \"ab\\\"#cdef\"", lineWithoutComment)

        lineWithoutComment = "#123".takeBeforeComment(false)
        assertEquals("", lineWithoutComment)

        lineWithoutComment = "key = \"ab\'c\"# ".takeBeforeComment(false)
        assertEquals("key = \"ab\'c\"", lineWithoutComment)

        lineWithoutComment = """
            a = 'C:\some\path\'#\abc
        """.trimIndent().takeBeforeComment(true)
        assertEquals("""a = 'C:\some\path\'""", lineWithoutComment)
    }

    @Test
    fun testForTrimComment() {
        var comment = "a = \"here#hash\" # my comment".trimComment(false)
        assertEquals("my comment", comment)

        comment = "a = \"here#\\\"hash\" # my comment".trimComment(false)
        assertEquals("my comment", comment)

        comment = " # my comment".trimComment(false)
        assertEquals("my comment", comment)

        comment = """
            a = 'C:\some\path\' #\abc
        """.trimIndent().trimComment(true)
        assertEquals("\\abc", comment)
    }
}
