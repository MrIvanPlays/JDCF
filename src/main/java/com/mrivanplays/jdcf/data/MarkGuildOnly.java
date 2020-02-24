package com.mrivanplays.jdcf.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a annotation, which marks whether or not the {@link com.mrivanplays.jdcf.Command} marked is
 * <i>guild-only</i>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MarkGuildOnly {
}
