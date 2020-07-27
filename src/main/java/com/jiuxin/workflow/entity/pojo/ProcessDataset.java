package com.jiuxin.workflow.entity.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author
 * 流程-数据集申请 关联信息
 */
@Data
public class ProcessDataset {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 流程状态
     */
    private Integer processStatus;

    /**
     * 数据集合ID 业务表主键
     */
    private String datasetId;

    /**
     * processInstance.getId()
     */
    private String procInstId;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * priority 优先级
     */
    private Integer priority;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
