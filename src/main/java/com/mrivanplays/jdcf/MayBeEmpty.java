package com.mrivanplays.jdcf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an annotation, which targets methods, which's return value is a {@link java.util.Collection} or {@link
 * String}, and which defines that the return value may be empty.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MayBeEmpty {
}
