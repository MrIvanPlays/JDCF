package com.mrivanplays.jdcf.data;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a annotation, which specifies a specific {@link com.mrivanplays.jdcf.Command}'s names.
 *
 * <h3>Usage example:</h3>
 *
 * <pre><code>
 *{@literal @CommandAliases("alias1|alias2|alias3")}
 * public class MyCommand extends Command {
 *    // other magical stuff happening here
 * }</code></pre>
 *
 * <p>Aliases should be represented like: "alias1|alias2|alias3"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAliases {

    /**
     * The aliases string mentioned in the annotation description.
     *
     * @return aliases, separated with "|"
     */
    @NotNull
    String value();
}
