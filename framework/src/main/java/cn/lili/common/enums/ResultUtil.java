package cn.lili.common.enums;

import cn.lili.common.vo.ResultMessage;

/**
 * 返回结果工具类
 *
 * @author sfy
 */
public class ResultUtil<T> {
    private final ResultMessage<T> resultMessage;
    /**
     * 正常响应默认状态码
     */
    private static final Integer SUCCESS = 200;

    /**
     * 构造ResultMessage，设置部分默认变量
     */
    public ResultUtil() {
        resultMessage = new ResultMessage<>();
        resultMessage.setSuccess(true);
        resultMessage.setMessage("success");
        resultMessage.setCode(SUCCESS);
    }

    /**
     * 设置传递的数据
     *
     * @param t 范型
     * @return 消息
     */
    public ResultMessage<T> setData(T t) {
        this.resultMessage.setResult(t);
        return this.resultMessage;
    }

    /**
     * 获得完整的ResultMessage对象
     *
     * @param t   范型
     * @param <T> 范型
     * @return 消息
     */
    public static <T> ResultMessage<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    /**
     * 设置成功返回的状态码和消息
     *
     * @param resultCode 返回码
     * @return 返回成功消息
     */
    public ResultMessage<T> setSuccessMsg(ResultCode resultCode) {
        this.resultMessage.setSuccess(true);
        this.resultMessage.setMessage(resultCode.message());
        this.resultMessage.setCode(resultCode.code());
        return this.resultMessage;

    }

    /**
     * 返回成功
     *
     * @param resultCode 返回状态码
     * @return 消息
     */
    public static <T> ResultMessage<T> success(ResultCode resultCode) {
        return new ResultUtil<T>().setSuccessMsg(resultCode);
    }

    /**
     * 返回成功，成功的方法是唯一的
     *
     * @return 消息
     */
    public static <T> ResultMessage<T> success() {
        return new ResultUtil<T>().setSuccessMsg(ResultCode.SUCCESS);
    }

    /**
     * 设置失败返回的状态码和消息
     *
     * @param resultCode 返回码
     * @return 消息
     */
    public ResultMessage<T> setErrorMsg(ResultCode resultCode) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMessage(resultCode.message());
        this.resultMessage.setCode(resultCode.code());
        return this.resultMessage;
    }

    /**
     * 自定义设置失败返回的状态码和消息
     *
     * @param code 状态码
     * @param msg  返回消息
     * @return 消息
     */
    public ResultMessage<T> setErrorMsg(Integer code, String msg) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMessage(msg);
        this.resultMessage.setCode(code);
        return this.resultMessage;
    }

    /**
     * 返回失败
     *
     * @param resultCode 返回状态码
     * @return 消息
     */
    public static <T> ResultMessage<T> error(ResultCode resultCode) {
        return new ResultUtil<T>().setErrorMsg(resultCode);
    }

    /**
     * 返回失败，自定义新的状态码和消息
     *
     * @param code 状态码
     * @param msg  返回消息
     * @return 消息
     */
    public static <T> ResultMessage<T> error(Integer code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }
}