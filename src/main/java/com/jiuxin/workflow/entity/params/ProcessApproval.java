package com.jiuxin.workflow.entity.params;

import com.jiuxin.workflow.entity.enums.ProcessApprovalEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author qcy
 * @date 2020年7月7日
 */
@Data
@ApiModel("流程审批参数")
public class ProcessApproval {

    @ApiModelProperty(value = "Event ID", required = true)
    @NotNull
    private String taskId;

    @ApiModelProperty(value = "审批状态: 1 同意，2 驳回，3 结束流程，强制终止", required = true)
    @NotNull
    @Pattern(regexp = "^(1|2|3)$", message = "{message.process.approve.option}")
    private String approvalStatus;


    @ApiModelProperty(value = "审批级别: ", required = true)
    @NotNull
    private String returnLevel;

    @ApiModelProperty(value = "审批意见")
    private String comment;

    /**
     * 当前审批人
     */
    @ApiModelProperty(hidden = true)
    private String assignee;

    @ApiModelProperty(value = "下个节点审批人")
    private String nextAssignee;

    @ApiModelProperty(hidden = true)
    public Integer getApproval() {
        return Integer.parseInt(approvalStatus);
    }

    @ApiModelProperty(hidden = true)
    public ProcessApprovalEnum getApprovalEnum() {
        return ProcessApprovalEnum.from(getApproval());
    }

}
