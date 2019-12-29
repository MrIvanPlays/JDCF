package com.mrivanplays.jdcf.data;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a annotation, which represents aliases of a {@link com.mrivanplays.jdcf.Command} Alias is basically a
 * (short) another name of a command. Should be annotated directly on the class implementing the parent command class
 * like:
 *
 * <h3>Usage example:</h3>
 *
 * <pre><code>
 *{@literal @CommandAliases("alias1|alias2|alias3")}
 * public class MyCommand extends Command
 * {
 *
 *     public MyCommand() // assuming this will be empty
 *     {
 *         super("commandname", Permission.MANAGE_SERVER);
 *     }
 *
 *     // other code
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
