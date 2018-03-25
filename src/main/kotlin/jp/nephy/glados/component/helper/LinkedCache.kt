package jp.nephy.glados.component.helper

import jp.nephy.glados.GLaDOS
import java.io.File
import java.nio.file.Files
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class LinkedSingleCache<T: Any>(private val klass: KClass<T>, private val default: () -> T) {
    private lateinit var current: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (! ::current.isInitialized) {
            current = read(property.name) ?: default()
        }
        return current
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        current = value
        write(property.name, value)
    }

    private fun getCacheFile(key: String): File {
        return GLaDOS.getTmpFile("cache", key)
    }

    private fun read(key: String): T? {
        val file = getCacheFile(key)
        if (! Files.exists(file.toPath())) {
            return null
        }
        val result = file.readText()
        if (result.isBlank() || result == "null") {
            return null
        }

        @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
        return when (klass) {
            Boolean::class -> result.toBoolean()
            Byte::class -> result.toByteOrNull()
            Char::class -> result.toCharArray().firstOrNull()
            Short::class -> result.toShortOrNull()
            Int::class -> result.toIntOrNull()
            Long::class -> result.toLongOrNull()
            Float::class -> result.toFloatOrNull()
            Double::class -> result.toDoubleOrNull()
            String::class -> result
            else -> throw NotImplementedError("${klass.qualifiedName} へのキャストは実装されていません。")
        } as? T
    }

    private fun write(key: String, value: T) {
        val file = getCacheFile(key)
        file.writeText(value.toString())
    }
}

open class LinkedListCache<T: Any>(private val klass: KClass<T>, private val default: () -> List<T>) {
    private lateinit var current: List<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        if (! ::current.isInitialized) {
            current = read(property.name) ?: default()
        }
        return current
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: List<T>) {
        current = value
        write(property.name, value)
    }

    private fun getCacheFile(key: String): File {
        return GLaDOS.getTmpFile("cache", key)
    }

    private fun read(key: String): List<T>? {
        val file = getCacheFile(key)
        if (! Files.exists(file.toPath())) {
            return null
        }
        val result = file.readLines()
        if (result.isEmpty()) {
            return null
        }

        @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
        return result.filter { it.isNotBlank() }.map {
            when (klass) {
                Boolean::class -> it.toBoolean()
                Byte::class -> it.toByteOrNull()
                Char::class -> it.toCharArray().firstOrNull()
                Short::class -> it.toShortOrNull()
                Int::class -> it.toIntOrNull()
                Long::class -> it.toLongOrNull()
                Float::class -> it.toFloatOrNull()
                Double::class -> it.toDoubleOrNull()
                String::class -> it
                else -> throw NotImplementedError("${klass.qualifiedName} へのキャストは実装されていません。")
            } as? T ?: return null
        }
    }

    private fun write(key: String, value: List<T>) {
        val file = getCacheFile(key)
        file.writeText(
                buildString {
                    value.forEach { appendln(it.toString()) }
                }
        )
    }
}

class BooleanLinkedSingleCache(default: () -> Boolean): LinkedSingleCache<Boolean>(Boolean::class, default)
class ByteLinkedSingleCache(default: () -> Byte): LinkedSingleCache<Byte>(Byte::class, default)
class CharLinkedSingleCache(default: () -> Char): LinkedSingleCache<Char>(Char::class, default)
class ShortLinkedSingleCache(default: () -> Short): LinkedSingleCache<Short>(Short::class, default)
class IntLinkedSingleCache(default: () -> Int): LinkedSingleCache<Int>(Int::class, default)
class LongLinkedSingleCache(default: () -> Long): LinkedSingleCache<Long>(Long::class, default)
class FloatLinkedSingleCache(default: () -> Float): LinkedSingleCache<Float>(Float::class, default)
class DoubleLinkedSingleCache(default: () -> Double): LinkedSingleCache<Double>(Double::class, default)
class StringLinkedSingleCache(default: () -> String): LinkedSingleCache<String>(String::class, default)

class BooleanLinkedListCache(default: () -> List<Boolean>): LinkedListCache<Boolean>(Boolean::class, default)
class ByteLinkedListCache(default: () -> List<Byte>): LinkedListCache<Byte>(Byte::class, default)
class CharLinkedListCache(default: () -> List<Char>): LinkedListCache<Char>(Char::class, default)
class ShortLinkedListCache(default: () -> List<Short>): LinkedListCache<Short>(Short::class, default)
class IntLinkedListCache(default: () -> List<Int>): LinkedListCache<Int>(Int::class, default)
class LongLinkedListCache(default: () -> List<Long>): LinkedListCache<Long>(Long::class, default)
class FloatLinkedListCache(default: () -> List<Float>): LinkedListCache<Float>(Float::class, default)
class DoubleLinkedListCache(default: () -> List<Double>): LinkedListCache<Double>(Double::class, default)
class StringLinkedListCache(default: () -> List<String>): LinkedListCache<String>(String::class, default)
