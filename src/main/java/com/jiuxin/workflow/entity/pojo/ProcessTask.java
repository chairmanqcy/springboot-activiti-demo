package com.jiuxin.workflow.entity.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author
 * 任务审批记录
 *
 * 可扩展说明 ：1、对于有文件上传的任务，可保存文件相关信息，如文件名，上传时间等
 *              2、对于需要发送邮件通知审批人的情况，可以保存收件人等
 */
@Data
public class ProcessTask {
    private Integer id;

    private String taskId;

    private String taskName;

    private String procInstId;

    private String approvalUser;

    private Integer approvalResult;

    private Date approvalTime;

    private Date createTime;

    private Date updateTime;

    private String approvalComment;


}
