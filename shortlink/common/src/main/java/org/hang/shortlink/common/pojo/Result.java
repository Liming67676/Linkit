package org.hang.shortlink.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hang.shortlink.common.enums.BaseErrorCode;
import org.hang.shortlink.common.exception.AbstractException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * 全局返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5679018624309023727L;

    /**
     * 正确返回码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;


    private Result(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public static<T> Result<T> success(String code, T data, String message){
        return new Result<>(code,data,message);
    }

    public static<T> Result<T> success(T data, String message){
        return new Result<>("0",data,message);
    }

    public static<T> Result<T> success(T data){
        return new Result<>("0",data,"success");
    }
    public static<T> Result<T> success(){
        return new Result<>("0",null,"success");
    }


    /**
     * 通过 errorCode、errorMessage 构建失败响应
     */
    public static<T> Result<T> failure(String errorCode,String errorMessage){
        return new Result<>(errorCode,null,errorMessage);
    }

    /**
     * 构建失败响应（仅传入消息，使用默认错误码）
     */
    public static<T> Result<T> failure(String errorMessage){
        return new Result<>(BaseErrorCode.SERVICE_ERROR.code(),null,errorMessage);
    }

    /**
     * 构建服务端失败响应
     */
    public static<T> Result<T> failure(){
        return new Result<>(BaseErrorCode.SERVICE_ERROR.code(),null,BaseErrorCode.SERVICE_ERROR.message());
    }

    /**
     * 通过 {@link AbstractException} 构建失败响应
     */
    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(abstractException.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<>(errorCode,null,errorMessage);
    }



}