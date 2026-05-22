/*
 * This file is part of code-gen, licensed under the MIT License.
 *
 * Copyright (c) 2026 Strokkur24
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
package net.strokkur.jap.code.test.util;

import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.type.CodeTypes;

public interface TestTypes extends ConvertToClassType {
  TestTypes JAVA_PLUGIN = create("org.bukkit.plugin.java.JavaPlugin");
  TestTypes PLUGIN_BOOTSTRAP = create("io.papermc.paper.plugin.bootstrap.PluginBootstrap");
  TestTypes BOOTSTRAP_CONTEXT = create("io.papermc.paper.plugin.bootstrap.BootstrapContext");

  TestTypes PLAYER = create("org.bukkit.entity.Player");

  TestTypes SIMPLE_COMMAND_EXCEPTION_TYPE = create("com.mojang.brigadier.exceptions.SimpleCommandExceptionType");
  TestTypes LITERAL_MESSAGE = create("com.mojang.brigadier.LiteralMessage");
  TestTypes COMMAND = create("com.mojang.brigadier.Command");

  // Custom
  TestTypes LIST_HOLDER = create("com.ListHolder");
  TestTypes CUSTOM_TYPE = create("com.CustomType");
  TestTypes MY_CLASS = create("com.MyClass");

  static TestTypes create(String fqn) {
    return () -> CodeTypes.ofClass(fqn);
  }
}
