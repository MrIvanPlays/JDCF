package com.mrivanplays.jdcf.data;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a annotation, which represents usage of a {@link com.mrivanplays.jdcf.Command}. Should be annotated
 * directly on the class implementing the parent command class like:
 *
 * <h3>Usage example:</h3>
 *
 * <pre><code>
 *{@literal @CommandUsage("hello [world] (123)")}
 * public class MyCommand extends Command
 * {
 *     // other code
 * }</code></pre>
 *
 * <p>tl;dr Make your command have a usage!
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandUsage {

    /**
     * Represents the usage string.
     *
     * @return usage
     */
    @NotNull
    String value();
}
