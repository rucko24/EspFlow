package com.nodemcuui.tool.data.util;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface IBuilder<T> {
    T make();
}
