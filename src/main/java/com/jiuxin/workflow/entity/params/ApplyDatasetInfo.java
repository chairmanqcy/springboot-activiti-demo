package com.jiuxin.workflow.entity.params;

import com.jiuxin.workflow.entity.enums.PriorityEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author
 * 申请数据集的基本信息
 */
@Data
@ToString
@ApiModel("申请数据集参数")
public class ApplyDatasetInfo {

    @ApiModelProperty(value = "数据集ID", required = true)
    @NotNull
    protected String dataSetId;

    @ApiModelProperty(value = "数据集名称", required = true)
    @NotNull
    protected String dataSetName;

    @ApiModelProperty(value = "数据集创建人", required = true)
    @NotNull
    protected String dataSetCreator;


    private String processDefKey;

    /**
     *  优先级
     */
    private PriorityEnum priority;

    public PriorityEnum getPriority() {
        if (priority == null) {
            return PriorityEnum.NOMAL;
        }
        return priority;
    }

}
