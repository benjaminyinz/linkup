package com.linkup.common.exception;

import com.linkup.common.result.Result;
import com.linkup.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * <p>它的作用是兜住 Controller / Service 抛出来的异常，并统一转换成 Result 响应。</p>
 *
 * <p>没有它时，每个接口都可能返回不同格式的错误；有了它之后，前端永远拿到类似下面的结构：</p>
 * <pre>
 * {
 *   "code": 2001,
 *   "message": "资源不存在",
 *   "data": null
 * }
 * </pre>
 *
 * <p>@RestControllerAdvice 可以理解成“全项目 Controller 的统一异常拦截器”。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * <p>业务异常是我们主动抛出的异常，例如未登录、没有权限、订单状态错误。
     * 这类异常通常不是系统故障，所以日志级别用 warn。</p>
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        log.warn("业务异常：code={}, message={}", exception.getCode(), exception.getMessage());
        return Result.build(null, exception.getCode(), exception.getMessage());
    }

    /**
     * 处理 @RequestBody 参数校验失败。
     *
     * <p>例如 DTO 字段上写了 @NotBlank、@Email，前端传来的 JSON 不符合规则时，
     * Spring 会抛出 MethodArgumentNotValidException。</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return buildParamErrorResult(exception.getBindingResult().getFieldError());
    }

    /**
     * 处理普通表单参数或查询参数绑定失败。
     *
     * <p>例如 GET 请求的 query 参数、表单参数无法绑定到对象时，可能会进入这里。</p>
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException exception) {
        return buildParamErrorResult(exception.getBindingResult().getFieldError());
    }

    /**
     * 处理必填请求参数缺失。
     *
     * <p>例如接口要求 ?page=1，但前端没有传 page，Spring 会抛出这个异常。</p>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String message = "缺少请求参数：" + exception.getParameterName();
        return Result.build(null, ResultCodeEnum.PARAM_VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 处理请求体 JSON 解析失败。
     *
     * <p>例如前端传了错误 JSON，或者字段类型不对：后端需要数字，前端传了字符串。</p>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return Result.fail(ResultCodeEnum.PARAM_VALIDATE_FAILED);
    }

    /**
     * 处理请求方法错误。
     *
     * <p>例如接口只支持 POST，但前端用了 GET。</p>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return Result.fail(ResultCodeEnum.ILLEGAL_REQUEST);
    }

    /**
     * 处理所有没有被上面规则捕获的异常。
     *
     * <p>这通常代表系统内部错误，例如空指针、数据库异常、第三方服务异常等。
     * 这里会记录完整错误日志，但返回给前端统一的“服务器内部错误”，避免泄漏内部细节。</p>
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("系统异常", exception);
        return Result.fail(ResultCodeEnum.SERVICE_ERROR);
    }

    /**
     * 从字段校验异常里提取最友好的错误提示。
     *
     * <p>如果能拿到字段上的自定义 message，就返回它；
     * 如果拿不到，就返回统一的“参数校验失败”。</p>
     */
    private Result<Void> buildParamErrorResult(FieldError fieldError) {
        String message = ResultCodeEnum.PARAM_VALIDATE_FAILED.getMessage();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            message = fieldError.getDefaultMessage();
        }
        return Result.build(null, ResultCodeEnum.PARAM_VALIDATE_FAILED.getCode(), message);
    }
}
