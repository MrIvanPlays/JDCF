/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.jdcf.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a annotation, which represents aliases of a {@link com.mrivanplays.jdcf.Command}
 * Alias is basically a (short) another name of a command. Should be annotated directly on the
 * class implementing the parent command class like:
 *
 * <blockquote>
 *
 * <pre>
 * <code>@CommandAliases("alias1|alias2|alias3")</code>
 * public class MyCommand extends Command
 * {
 *
 *     public MyCommand() // assuming this will be empty
 *     {
 *         super("commandname", Permission.MANAGE_SERVER);
 *     }
 *
 *     // other code
 * }
 * </pre>
 *
 * </blockquote>
 *
 * <p>Aliases should be represented like: "alias1|alias2|alias3"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAliases
{

    /**
     * The aliases string mentioned in the annotation description.
     *
     * @return aliases, separated with "|"
     */
    @NotNull
    String value();
}
