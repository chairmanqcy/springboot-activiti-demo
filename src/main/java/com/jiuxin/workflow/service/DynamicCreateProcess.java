package com.jiuxin.workflow.service;

import com.jiuxin.workflow.entity.ModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DynamicCreateProcess {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 开始任务节点
     * @return
     */
    protected StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        return startEvent;
    }


    /**
     * 结束任务节点
     * @return
     */
    protected EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end");
        return endEvent;
    }

    /**
     *@param id  对应我们画流程图中节点任务id
     * @param name 节点任务名称
     * @param assignee 任务的执行者(这一块自行决定是否添加每一环节的执行者，若是动态分配的话，可以不用传值)
     * @return
     */
    protected UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }


    /**
     * @param id  网关id
     * @return
     */
    protected static ExclusiveGateway createExclusiveGateway(String id) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        return exclusiveGateway;
    }

    /**
     *
     * @param from         连线来源节点
     * @param to        连线目标节点
     * @param name          连线名称(可不填)
     * @param conditionExpression  网关每一种线路走向的条件表达式
     * @return
     */
    protected SequenceFlow createSequenceFlow(String from, String to, String name, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setName(name);
        if (StringUtils.isNotEmpty(conditionExpression)) {
            flow.setConditionExpression(conditionExpression);
        }
        return flow;
    }


    /**
     * 动态创建流程
     * @return
     */
    public String dynamicCreateProcess(String processId,List<ModelEntity> taskList){
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        model.addProcess(process);
        process.setId(processId);
        // 判断是否仅为一个节点任务
        if(taskList.size() == 1){
            process.addFlowElement(createStartEvent());
            process.addFlowElement(createUserTask("task1", taskList.get(0).getTaskName(), taskList.get(0).getAssignee()));
            process.addFlowElement(createEndEvent());
            process.addFlowElement(createSequenceFlow("start", "task1", "", ""));
            process.addFlowElement(createSequenceFlow("task1", "end", "", ""));
        }else{
            // 多节点任务
            // 构造开始节点任务
            process.addFlowElement(createStartEvent());
            // 构造首个节点任务
            process.addFlowElement(createUserTask("task1", taskList.get(0).getTaskName(), taskList.get(0).getAssignee()));
            // 构造除去首尾节点的任务
            for (int i = 1; i < taskList.size() - 1; i++) {
                process.addFlowElement(createExclusiveGateway("createExclusiveGateway" + i));
                process.addFlowElement(createUserTask("task" + (i + 1), taskList.get(i).getTaskName(), taskList.get(i).getAssignee()));
            }
            // 构造尾节点任务
            process.addFlowElement(createExclusiveGateway("createExclusiveGateway" + (taskList.size() - 1)));
            process.addFlowElement(createUserTask("task" + taskList.size(), taskList.get(taskList.size() - 1).getTaskName(),
                    taskList.get(taskList.size() - 1).getAssignee()));
            // 构造结束节点任务
            process.addFlowElement(createEndEvent());

            // 构造连线(加网关)
            process.addFlowElement(createSequenceFlow("start", "task1", "", ""));
            // 第一个节点任务到第二个百分百通过的，因此不存在网关
            process.addFlowElement(createSequenceFlow("task1", "task2", "", ""));
            for (int i = 1; i < taskList.size(); i++) {
                process.addFlowElement(createSequenceFlow("task" + (i + 1), "createExclusiveGateway" + i, "", ""));
                // 判断网关走向(同意则直接到下一节点即可，不同意需要判断回退层级，决定回退到哪个节点，returnLevel等于0，即回退到task1)
                // i等于几，即意味着回退的线路有几种可能，例如i等于1，即是task2,那么只能回退 到task1
                // 如果i等于2，即是task3,那么此时可以回退到task1和task2;returnLevel =1 ，即回退到task1，所以这里我是扩展了可以驳回到任意阶段节点任务
                for (int j = 1; j <= i; j++) {
                    process.addFlowElement(createSequenceFlow("createExclusiveGateway" + i, "task" + j, "不通过",
                            "${approvalStatus == '0' && returnLevel== '" + j + "'}"));
                }
            // 操作结果为通过时，需要判断是否为最后一个节点任务，若是则直接到end
                if (i == taskList.size() - 1) {
                    process.addFlowElement(
                            createSequenceFlow("createExclusiveGateway" + i, "end", "通过", "${approvalStatus == '1'} "));

                } else {
                    process.addFlowElement(createSequenceFlow("createExclusiveGateway" + i, "task" + (i + 2), "通过",
                            "${approvalStatus == '1'}"));
                }

            }

        }
        //3.生成图像信息
        new BpmnAutoLayout(model).execute();

        //4.部署流程
        Deployment deployment = repositoryService.createDeployment().addBpmnModel("dynamic-model.bpmn", model)
                .name("multiple process deployment").deploy();
        //5.启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processId);

//        try {
//            // 6. 保存圖片
//            InputStream processDiagram = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
//            FileUtils.copyInputStreamToFile(processDiagram, new File("target/multiple-process3-diagram.png"));
//
//            // 7. 保存bpmn
//            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), "dynamic-model.bpmn");
//            FileUtils.copyInputStreamToFile(processBpmn, new File("target/multiple-process3.bpmn20.xml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return processInstance.getId();
    }




}
