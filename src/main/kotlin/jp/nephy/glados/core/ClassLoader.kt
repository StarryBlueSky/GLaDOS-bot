/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.nephy.glados.core

import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.Logger
import jp.nephy.glados.api.of
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.streams.asSequence

private val logger = Logger.of("GLaDOS.ClassLoader")

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T: Any> loadClassesFromJar(path: Path): List<KClass<T>> {
    val thread = Thread.currentThread()
    thread.addClassPath(path)

    return JarFile(path.toFile()).use { file ->
        file.stream().asSequence().filter {
            it.name.endsWith(".class") && '$' !in it.name
        }.map {
            it.name.removeSuffix(".class").replace('/', '.')
        }.mapNotNull {
            runCatching {
                thread.contextClassLoader.loadClass(it)
            }.onSuccess {
                if (it.canonicalName == null) {
                    return@mapNotNull null
                }
                
                logger.trace { "クラス: \"${it.canonicalName}\" をロードしました。($path)" }
            }.onFailure {  e ->
                logger.trace(e) { "クラス: \"$it\" のロードに失敗しました。($path)" }
            }.getOrNull()
        }.filter { 
            T::class.java.isAssignableFrom(it) && !it.isInterface
        }.mapNotNull {
            it.kotlin as? KClass<T>
        }.filter {
            !it.isAbstract
        }.toList()
    }
}

private fun Thread.addClassPath(jarPath: Path) {
    val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
    method.isAccessible = true
    method.invoke(contextClassLoader, jarPath.toUri().toURL())
}

private const val definitionPath = "META-INF/clients"

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T: Any> loadClassesFromClassPath(): List<KClass<T>> {
    val classLoader = Thread.currentThread().contextClassLoader
    val roots = GLaDOS::class.java.classLoader.getResource(definitionPath)?.let { listOf(it) }
        ?: classLoader.getResources(definitionPath).toList()
    
    return roots.flatMap {  root ->
        logger.trace { "クラスパス: \"$root\" を検出しました。" }

        when (root.protocol) {
            "file" -> {
                val paths = arrayListOf<Path>()
                Files.walkFileTree(Paths.get(root.toURI()), object: SimpleFileVisitor<Path>() {
                    override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                        paths.add(path)
                        return FileVisitResult.CONTINUE
                    }
                })

                paths.map {
                    it.fileName.toString()
                }
            }
            "jar" -> {
                (root.openConnection() as JarURLConnection).jarFile.use {
                    it.entries().toList()
                }.filter {
                    it.name.startsWith(definitionPath)
                }.map {
                    it.name.removePrefix("$definitionPath/")
                }.filter { 
                    it.isNotBlank()
                }
            }
            else -> {
                throw UnsupportedOperationException("Unknown protocol: ${root.protocol}")
            }
        }
    }.asSequence().mapNotNull {
        runCatching {
            classLoader.loadClass(it)
        }.onSuccess {
            if (it.canonicalName == null) {
                return@mapNotNull null
            }

            logger.trace { "クラス: \"${it.canonicalName}\" をロードしました。" }
        }.onFailure { e ->
            logger.error(e) { "クラス: \"$it\" のロードに失敗しました。" }
        }.getOrNull()
    }.filter {
        T::class.java.isAssignableFrom(it) && !it.isInterface
    }.mapNotNull {
        it.kotlin as? KClass<T>
    }.filter {
        !it.isAbstract
    }.toList()
}