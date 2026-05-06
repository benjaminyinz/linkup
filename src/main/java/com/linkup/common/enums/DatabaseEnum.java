package com.linkup.common.enums;

/**
 * 数据库枚举接口。
 *
 * <p>PostgreSQL 里的 enum 值通常是小写字符串，例如 {@code open}、{@code fixed}。
 * Java 枚举常量通常是大写，例如 {@code OPEN}、{@code FIXED}。</p>
 *
 * <p>实现这个接口后，通用 TypeHandler 就知道应该把 Java 枚举转换成哪个数据库值。</p>
 */
public interface DatabaseEnum {

    /**
     * 数据库中真实保存的 enum 值。
     *
     * @return PostgreSQL enum 字符串
     */
    String getValue();
}
