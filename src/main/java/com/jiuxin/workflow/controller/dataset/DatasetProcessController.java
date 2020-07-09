package com.jiuxin.workflow.controller.dataset;

import com.github.pagehelper.PageInfo;
import com.jiuxin.workflow.entity.params.ApplyDatasetInfo;
import com.jiuxin.workflow.entity.params.ProcessApproval;
import com.jiuxin.workflow.entity.query.BasePageQuery;
import com.jiuxin.workflow.service.DatasetProcessService;
import com.jiuxin.workflow.entity.Response;
import com.jiuxin.workflow.entity.pojo.ProcessTask;
import com.jiuxin.workflow.entity.vo.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author qcy
 * @date 2020年7月9日
 */
@Api(tags = "数据集流程接口")
@Slf4j
@RestController
@RequestMapping("/process/dataset")
public class DatasetProcessController {

    @Autowired
    private DatasetProcessService datasetProcessService;

    @ApiOperation(value = "申请数据集")
    @PostMapping("applyDataSet")
    public Response applyDataSet(@RequestBody @Validated ApplyDatasetInfo datasetBaseInfo, HttpServletRequest request) {
        TaskVo firstTaskVo = datasetProcessService.applyDataSet(datasetBaseInfo, request);
        return Response.success(firstTaskVo);
    }

    @ApiOperation("获取用户需要处理的Task")
    @GetMapping("getUserTask")
    public Response getUserTask(BasePageQuery pageQuery, HttpServletRequest request) {
        PageInfo<TaskVo> pageInfo = datasetProcessService.getUserTask(pageQuery, request);
        return Response.success(pageInfo);
    }

    @ApiOperation("审批任务")
    @PostMapping("approvalTask")
    public Response approvalTask(@RequestBody @Validated ProcessApproval processApproval, HttpServletRequest request) {
        datasetProcessService.approvalTask(processApproval, request);
        return Response.success();
    }

    @ApiOperation("获取审批历史记录")
    @GetMapping("getApprovalHistory")
    public Response getApprovalHistory(String processInstanceId) {
        List<ProcessTask> taskApprovalHisroties = datasetProcessService.getApprovalHistory(processInstanceId);
        return Response.success(taskApprovalHisroties);
    }

    @ApiOperation("终止流程")
    @PostMapping("stopProcess")
    public Response stopProcess(@RequestBody @Validated ProcessApproval processApproval, HttpServletRequest request) {
        datasetProcessService.stopProcess(processApproval, request);
        return Response.success();
    }

}
