/*
 * This file is part of test-ap-project, licensed under the MIT License.
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
package net.strokkur;

public class Main {
  public static void main(String[] args) {
    PersonBuilder builder = PersonBuilder.create()
      .setFirstName("Andrew")
      .setLastName("Holland");

    System.out.println();
    System.out.println("The default age is: " + builder.getAge() + ". That doesn't seem right...\n");
    builder.setAge(24);

    Person andrew = builder.build();
    System.out.printf(
      "Allow me to welcome %s %s. %s, but his favorite food is: %s. He is %d years old!%n", andrew.firstName(),
      andrew.lastName(),
      andrew.address() == null ? "I won't say his address" : "He lives at " + andrew.address(),
      andrew.favoriteFood(),
      andrew.age()
    );
  }
}
