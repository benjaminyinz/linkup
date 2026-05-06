package com.linkup.common.exception;

import com.linkup.common.result.ResultCodeEnum;
import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>它用来表示“程序没有坏，但是业务规则不允许继续”的情况。
 * 例如：用户未登录、资源不存在、活动人数已满、订单状态不允许操作。</p>
 *
 * <p>业务代码里只需要抛出这个异常，GlobalExceptionHandler 会统一把它转换成前端需要的 Result JSON。
 * 这样每个 Controller / Service 就不需要自己写 try-catch 和返回格式了。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码。
     *
     * <p>这个 code 会原样返回给前端，例如 1001 表示未登录，2001 表示资源不存在。</p>
     */
    private final Integer code;

    /**
     * 使用统一错误码枚举创建业务异常。
     *
     * <p>最常用写法：</p>
     * <pre>
     * throw new BusinessException(ResultCodeEnum.RESOURCE_NOT_FOUND);
     * </pre>
     */
    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    /**
     * 使用统一错误码，但自定义这一次返回给前端的错误文案。
     *
     * <p>适合错误码类型固定，但提示需要更具体的场景。
     * 例如仍然是参数错误，但想提示“邮箱格式不正确”。</p>
     */
    public BusinessException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.code = resultCodeEnum.getCode();
    }

    /**
     * 直接指定错误码和错误文案。
     *
     * <p>一般不优先使用这个构造方法。能用 ResultCodeEnum 时，优先使用枚举，
     * 这样错误码会更统一，也更容易维护。</p>
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
