package net.strokkur.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper

abstract class RemapClassesTask : DefaultTask() {

  @get:Input
  @get:Optional
  abstract val prefix: Property<String>

  @get:InputDirectory
  @get:Optional
  abstract val sourceDir: DirectoryProperty

  @get:OutputDirectory
  abstract val targetDir: DirectoryProperty

  @TaskAction
  fun remapClasses() {
    sourceDir.asFileTree
      .matching { include("**/*.class") }
      .forEach { file ->
        val remapped = remapClass(file.readBytes())
        val target = targetDir.file(remapped.filename).get().asFile
        target.parentFile.mkdirs()
        target.writeBytes(remapped.bytes)
      }
  }

  private fun remapClass(input: ByteArray): RemapResult {
    val reader = ClassReader(input)
    val remapper = object : Remapper(Opcodes.ASM9) {
      override fun mapType(internalName: String?): String? {
        return if (internalName?.startsWith(prefix.getOrElse("generated/")) ?: false) {
          internalName.removePrefix(prefix.getOrElse("generated/")).removeSuffix("A")
        } else {
          internalName
        }
      }
    }

    val writer = ClassWriter(0)
    reader.accept(
      ClassRemapper(writer, remapper),
      ClassReader.EXPAND_FRAMES
    )

    return RemapResult(writer.toByteArray(), reader.className.removePrefix(prefix.getOrElse("generated/")).removeSuffix("A") + ".class")
  }

  private class RemapResult(
    val bytes: ByteArray,
    val filename: String,
  )
}
