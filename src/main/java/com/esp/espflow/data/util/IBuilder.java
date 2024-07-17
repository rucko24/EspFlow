package com.esp.espflow.data.util;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface IBuilder<T> {
    T make();
}
