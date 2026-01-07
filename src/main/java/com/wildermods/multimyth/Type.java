package com.wildermods.multimyth;

import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents the intended static type of an annotated element when the actual
 * Java type must be broader (typically {@code Object}) for technical reasons.
 * <p>
 * This annotation exists primarily to improve readability and maintainability
 * in situations where bytecode level tooling (such as Mixins) imposes type
 * erasure or signature constraints that obscure the real semantic type.
 * <p>
 * A common use case is mixin injections into generic or erased methods, where
 * the JVM signature is {@code Object}, but the value is logically known
 * to be a more specific type.
 * <p>
 *
 * This annotation has no runtime effect and is intended solely for human readers
 * and static analysis tools.
 */
@Retention(RetentionPolicy.CLASS)
@Target(TYPE_USE)
public @interface Type {
	Class value();
}
