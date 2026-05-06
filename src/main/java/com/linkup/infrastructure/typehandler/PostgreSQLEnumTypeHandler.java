package com.linkup.infrastructure.typehandler;

import com.linkup.common.enums.DatabaseEnum;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * PostgreSQL enum 类型处理器。
 *
 * <p>PostgreSQL enum 不是普通 varchar。
 * 如果直接用字符串写入，某些场景会报“字段是 activity_status，但参数是 varchar”这类错误。
 * 所以写入时要使用 {@link PreparedStatement#setObject(int, Object, int)}，
 * 并把 JDBC 类型指定为 {@link Types#OTHER}。</p>
 *
 * <p>这个处理器是通用的，只要 Java 枚举实现 DatabaseEnum，就可以复用它。</p>
 */
public class PostgreSQLEnumTypeHandler<E extends Enum<E> & DatabaseEnum> extends BaseTypeHandler<E> {

    private final Class<E> enumType;

    public PostgreSQLEnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("枚举类型不能为空");
        }
        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement,
                                    int index,
                                    E parameter,
                                    JdbcType jdbcType) throws SQLException {
        preparedStatement.setObject(index, parameter.getValue(), Types.OTHER);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return parse(resultSet.getString(columnName));
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return parse(resultSet.getString(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return parse(callableStatement.getString(columnIndex));
    }

    private E parse(String value) throws SQLException {
        if (value == null) {
            return null;
        }

        for (E enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }

        throw new SQLException("未知的 PostgreSQL enum 值：" + value + "，目标 Java 枚举：" + enumType.getName());
    }
}
