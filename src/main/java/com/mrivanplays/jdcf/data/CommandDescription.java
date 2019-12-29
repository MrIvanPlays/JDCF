package com.mrivanplays.jdcf.data;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a annotation, which represents description of a {@link com.mrivanplays.jdcf.Command}. Should be annotated
 * directly on the class implementing the parent command class like:
 *
 * <h3>Usage example:</h3>
 *
 * <pre><code>
 *{@literal @CommandDescription("This command does all sorts of things!")}
 * public class MyCommand extends Command
 * {
 *     // other code
 * }</code></pre>
 *
 * <p>tl;dr Give your command an awesome description!
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDescription {

    /**
     * Represents the description you want your command to have.
     *
     * @return description
     */
    @NotNull
    String value();
}
