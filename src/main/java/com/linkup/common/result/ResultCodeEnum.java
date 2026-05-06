package com.linkup.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(0, "ok"),
    FAIL(1, "fail"),

    UNAUTHORIZED(1001, "未登录或 token 过期"),
    EMAIL_ALREADY_REGISTERED(1002, "邮箱已注册"),
    EMAIL_OR_PASSWORD_ERROR(1003, "邮箱或密码错误"),
    PARAM_VALIDATE_FAILED(1004, "参数校验失败"),
    FORBIDDEN(1005, "没有权限"),
    REPEAT_SUBMIT(1006, "重复提交"),
    ILLEGAL_REQUEST(1007, "非法请求"),

    RESOURCE_NOT_FOUND(2001, "资源不存在"),
    DATA_ERROR(2002, "数据异常"),
    DUPLICATE_OPERATION(2003, "重复操作"),
    ACTIVITY_FULL(2101, "活动人数已满"),
    ACTIVITY_NOT_OPEN(2102, "活动不可加入"),
    LEVEL_NOT_ENOUGH(2103, "用户等级不足"),
    COUPON_ALREADY_CLAIMED(2201, "优惠券已经领取"),
    COUPON_LIMIT_REACHED(2202, "优惠券已发放完毕"),

    ORDER_STATUS_ERROR(3001, "订单状态不允许操作"),
    ORDER_PRICE_CHANGED(3002, "订单商品价格已变化"),
    ORDER_STOCK_LOCK_FAILED(3003, "订单库存锁定失败"),
    CREATE_ORDER_FAILED(3004, "创建订单失败"),
    PAYMENT_WAITING(3101, "订单支付中"),
    PAYMENT_SUCCESS(3102, "订单支付成功"),
    PAYMENT_FAILED(3103, "订单支付失败"),
    PAYMENT_CALLBACK_INVALID(3104, "非法支付回调请求"),

    URL_ENCODE_ERROR(4001, "URL 编码失败"),
    FETCH_ACCESS_TOKEN_FAILED(4002, "获取 accessToken 失败"),
    FETCH_USER_INFO_FAILED(4003, "获取用户信息失败"),

    SERVICE_ERROR(5000, "服务器内部错误");

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
