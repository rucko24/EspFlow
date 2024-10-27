package com.esp.espflow.util;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface IBuilder<T> {
    T make();
}
