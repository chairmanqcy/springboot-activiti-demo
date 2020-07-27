package com.jiuxin.workflow.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiuxin.workflow.dao.ProcessDatasetMapper;
import com.jiuxin.workflow.dao.ProcessTaskMapper;
import com.jiuxin.workflow.entity.enums.ProcessStatusEnum;
import com.jiuxin.workflow.entity.params.ApplyDatasetInfo;
import com.jiuxin.workflow.entity.params.ProcessApproval;
import com.jiuxin.workflow.entity.pojo.ProcessDataset;
import com.jiuxin.workflow.entity.pojo.ProcessTask;
import com.jiuxin.workflow.entity.query.BasePageQuery;
import com.jiuxin.workflow.entity.vo.TaskVo;
import com.jiuxin.workflow.exception.GlobalException;
import com.jiuxin.workflow.service.process.ProcessRuntimeService;
import com.jiuxin.workflow.service.process.ProcessTaskService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jiuxin.workflow.utils.WorkflowConstants.*;

/**
 * 数据集-流程相关的Service
 */
@Service
@Slf4j
public class DatasetProcessService {

    @Autowired
    private ProcessRuntimeService processRuntimeService;

    @Autowired
    private ProcessTaskService processTaskService;

    @Autowired
    private ProcessDatasetMapper processDatasetMapper;

    @Autowired
    private ProcessTaskMapper processTaskMapper;

    /**
     * 获取用户需要处理的任务
     * @param currentUser
     * @return
     */
    public PageInfo<TaskVo> getUserTask(BasePageQuery pageQuery, String  currentUser) {



        // 分页查询用户的任务
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<TaskVo> taskVoPage = processDatasetMapper.getTasksByAssignee(currentUser);

        return new PageInfo(taskVoPage);
    }

    /**
     * 申请数据集
     *
     * @param datasetBaseInfo
     * @param currentUser
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskVo applyDataSet(ApplyDatasetInfo datasetBaseInfo, String currentUser) {

        // TODO 获取当前用户相关的流程实例，如果当前用户没有创建的流程，则使用默认流程
        String processDefKey;
        if(StringUtils.isNotEmpty(datasetBaseInfo.getProcessDefKey())){
            processDefKey = datasetBaseInfo.getProcessDefKey();
        }else{
            processDefKey = DEFAULT_DS_PROCESS_KEY;
        }

        // 1、启动流程实例
       // ProcessInstance processInstance = processRuntimeService.startProcessInstanceByKey(processDefKey);

        ProcessInstance processInstance = processRuntimeService.getProcessInstance(processDefKey);


        // 2、新增流程实例和业务关联信息
        ProcessDataset processDataset = new ProcessDataset();
        processDataset.setDatasetId(datasetBaseInfo.getDataSetId());
        processDataset.setDatasetName(datasetBaseInfo.getDataSetName());
        processDataset.setCreator(currentUser);
        processDataset.setPriority(datasetBaseInfo.getPriority().getCode());
        processDataset.setProcInstId(processInstance.getId());
        processDataset.setProcessStatus(ProcessStatusEnum.ONGOING.getCode());
        processDatasetMapper.insertSelective(processDataset);

        // 3、如果是默认审批流程实例，则设置数据集创建人为审批人
        Task task = processTaskService.getTaskByProInstId(processInstance.getId()).get(0);
        if (StringUtils.equals(processInstance.getProcessDefinitionKey(), DEFAULT_DS_PROCESS_KEY)) {
            processTaskService.assigneeTask(task.getId(), datasetBaseInfo.getDataSetCreator());
        }

        return new TaskVo(task);
    }

    /**
     * 审批任务
     *
     * @param processApproval
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void approvalTask(ProcessApproval processApproval, String currentUser) {

        // 获取任务信息
        Task task = processTaskService.getTaskByTaskId(processApproval.getTaskId());

        validateTaskFinished(task);

        // 获取任务指定处理人
        String assignee = task.getAssignee();

        // 判断当前处理人是否和任务指派人一致
        if (!StringUtils.equals(currentUser, assignee)) {
            throw new GlobalException("当前任务审批人应该是: " + assignee);
        }

        // 处理任务，并设置审批流程变量，用户网关控制下一任务
        Map<String, Object> variables = new HashMap<>(16);
        variables.put(APPROVAL_RESULT_VARIABLE_NAME, processApproval.getApprovalEnum().getCode());
        variables.put(APPROVAL_LEVEL_VARIABLE_NAME,processApproval.getReturnLevel());
        processTaskService.completeTask(task.getId(), variables);
        log.info("variables:{}", variables);

        // 记录当前任务审批相关信息
        saveTaskApprovalInfo(processApproval, currentUser, task);

        // 更新流程状态
        updateProcessStatus(task.getProcessInstanceId());
    }

    /**
     * 记录当前任务审批相关信息
     *
     * @param processApproval
     * @param currentUser
     * @param task
     */
    private void saveTaskApprovalInfo(ProcessApproval processApproval, String currentUser, Task task) {
        ProcessTask processTask = new ProcessTask();
        processTask.setTaskId(task.getId());
        processTask.setProcInstId(task.getProcessInstanceId());
        processTask.setTaskName((task.getName() == null) ? "" : task.getName());
        processTask.setApprovalUser(currentUser);
        processTask.setApprovalTime(new Date());
        processTask.setApprovalResult(processApproval.getApprovalEnum().getCode());
        processTask.setApprovalComment(processApproval.getComment());
        processTaskMapper.insertSelective(processTask);
    }

    /**
     * 如果流程已经结束，则更新流程状态
     *
     * @param processInstanceId
     */
    private void updateProcessStatus(String processInstanceId) {
        boolean isEnd = processRuntimeService.processIsEnd(processInstanceId);
        if (isEnd) {
            updateProcessStatus(processInstanceId, ProcessStatusEnum.FINISHED);
        }
    }

    /**
     * 更新流程状态
     *
     * @param processInstanceId
     * @param processStatus
     */
    private void updateProcessStatus(String processInstanceId, ProcessStatusEnum processStatus) {
        ProcessDataset processDataset = processDatasetMapper.selectByProcessInstanceId(processInstanceId);
        ProcessDataset newProcessDataset = new ProcessDataset();
        newProcessDataset.setId(processDataset.getId());
        newProcessDataset.setProcessStatus(processStatus.getCode());
        processDatasetMapper.updateByPrimaryKeySelective(newProcessDataset);
    }

    /**
     * 获取流程审批历史记录
     *
     * @param processInstanceId
     * @return
     */
    public List<ProcessTask> getApprovalHistory(String processInstanceId) {
        List<ProcessTask> taskApprovalList = processTaskMapper.selectTaskByProcessInstanceId(processInstanceId);
        return taskApprovalList;
    }

    /**
     * 终止流程
     * <p>
     * 说明：这里的挂起，并不是让流程走完，而是通过设置流程状态来实现
     *
     * @param processApproval
     * @param currentUser
     */
    public void stopProcess(ProcessApproval processApproval, String currentUser) {

        Task task = processTaskService.getTaskByTaskId(processApproval.getTaskId());
        validateTaskFinished(task);

        String processInstanceId = task.getProcessInstanceId();

        // 将流程挂起
        processRuntimeService.suspendProcess(processInstanceId);

        // 更新流程状态为强制终止
        updateProcessStatus(processInstanceId, ProcessStatusEnum.CLOSED);

        // 添加当前任务处理纪录为终止
        saveTaskApprovalInfo(processApproval, currentUser, task);
    }

    /**
     * 验证任务是否已经完成
     *
     * @param task
     */
    private void validateTaskFinished(Task task) {
        if (task == null) {
            throw new GlobalException("该任务已完成或不存在，请刷新页面后重试");
        }
    }
}
