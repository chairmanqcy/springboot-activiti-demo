package com.jiuxin.workflow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiuxin.workflow.constant.DateConst;
import com.jiuxin.workflow.constant.LogConst;
import com.jiuxin.workflow.entity.enums.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.LocalDateTime;


/**
 * 通用返回对象
 *
 * @author C.K.Y
 */
@Slf4j
@NoArgsConstructor
@Data
public class ResponseEntity<T> {

    /**
     * 状态码，请查看{@link ErrorCode}
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 时间戳
     */
    @JsonFormat(pattern = DateConst.yyyy_MM_dd_HH_mm_ss)
    private LocalDateTime timestamp;

    public ResponseEntity(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.requestId = MDC.get(LogConst.REQUEST_ID);
        this.timestamp = LocalDateTime.now();
    }

    public ResponseEntity(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.requestId = MDC.get(LogConst.REQUEST_ID);
        this.timestamp = LocalDateTime.now();
    }

    public static ResponseEntity<Void> ok(String msg) {
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), msg);
    }

    public static <T> ResponseEntity<T> ok(T data) {
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), data);
    }

    public static <T> ResponseEntity<T> ok(String msg, T body) {
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), msg, body);
    }

    public static ResponseEntity<Void> fail(ErrorCode errorCode) {
        return new ResponseEntity<>(errorCode.getCode(), errorCode.getMsg());
    }

    public static ResponseEntity<Void> fail(ErrorCode errorCode, String msg) {
        return new ResponseEntity<>(errorCode.getCode(), msg);
    }

    public static <T> ResponseEntity<T> fail(ErrorCode errorCode, String msg, T data) {
        return new ResponseEntity<>(errorCode.getCode(), msg, data);
    }
}
