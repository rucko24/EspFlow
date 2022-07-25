package com.nodemcutools.application.data.util;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface IBuilder<T> {
    T make();
}
