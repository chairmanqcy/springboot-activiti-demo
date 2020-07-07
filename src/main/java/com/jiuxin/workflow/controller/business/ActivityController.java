package com.jiuxin.workflow.controller.business;

import com.jiuxin.workflow.entity.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author qcy
 * @date 2020-7-7
 * 基础流程
 */
@RequestMapping("/activity")
@RestController
@Slf4j
public class ActivityController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    /**
     * 第一步：部署流程定义
     * @param deployName 部署名称
     * @param url 资源路径
     * 输出：
     * 部署ID：
     * 部署名称：
     * 部署Key：null
     */
    @PostMapping("/deploy")
    public ResponseEntity deploymentProcessDefinition(@RequestParam String deployName , @RequestParam String url) {


        deployName = "hello_jiuxin";
        url = " processes/helloworld.bpmn";

        //创建一个部署对象
        Deployment deployment = repositoryService
                .createDeployment()
                //添加部署的名称
                .name(deployName)
                //从classpath的资源中加载，一次只能加载一个文件
                .addClasspathResource(url)
                //完成部署
                .deploy();
        // 这个key并不是我们画流程里面的key
        log.info("部署ID：{},部署名称：{}，部署Key：{}",deployment.getId(),deployment.getName(),deployment.getKey());

        return ResponseEntity.ok("部署ID："+deployment.getId()+"," +
                "部署名称："+deployment.getName()+"，部署Key："+deployment.getKey());

    }

    /**
     * 第二步：启动流程实例
     * @param processKey 流程key
     * 流程实例ID_demo:5001
     * 流程定义ID_demo:helloworld:1:2504
     */
    @PostMapping("/run")
    public ResponseEntity startProcessInstance(@RequestParam String processKey) {

        processKey = "helloworld";
        //根据流程定义的key启动流程
        String processDefinitionKey = processKey;
        //使用流程定义的key启动流程实例，key对应bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
        ProcessInstance pi = runtimeService
                .startProcessInstanceByKey(processDefinitionKey);

        log.info("流程实例ID:{}，流程定义ID:{}",pi.getId(),pi.getProcessDefinitionId());

       return  ResponseEntity.ok("流程实例ID:"+pi.getId()+"，流程定义ID:"+pi.getProcessDefinitionId());

    }

    /**
     * 第三步：查询当前人的个人任务
     * @param  assignee 提交的審批人
     * @param  processInstanceId 流程实例ID
     */
    @PostMapping
    public ResponseEntity findMyPersonalTask(@RequestParam String assignee , @RequestParam String processInstanceId) {
        assignee = "王五";
        processInstanceId = "25001";
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        log.info("当前任务处理人：{},指派任务新处理人：{}",taskList.get(0).getAssignee(),assignee);
        taskService.setAssignee(taskList.get(0).getId(), assignee);
        // 查询新处理人的任务
        List<Task> list = taskService
                //创建任务查询对象
                .createTaskQuery()
                //指定个人任务查询，指定办理人
                .taskAssignee(assignee)
                .list();
        printTaskInfo(list);
        return ResponseEntity.ok(list);

    }


    /**
     * 第四步：完成我的任务
     * @param taskId 任务ID
     */
    @PostMapping("/complete")
    public ResponseEntity<Void> completeMyPersonalTask(@RequestParam String taskId, @RequestParam Map<String, Object> variables) {
        //任务ID
        taskId = "5005";
        if(variables.isEmpty()){
            taskService.complete(taskId);
        }else {
            taskService.complete(taskId,variables);
        }

        return ResponseEntity.ok("完成任务，任务ID = ："+taskId);

    }


    /**
     * 查询当前任务
     * @param processInstanceId
     */
    public ResponseEntity queryCurrentTask(@RequestParam String processInstanceId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId("5001").list();
        printTaskInfo(list);
        return ResponseEntity.ok(list);
    }





    private void printTaskInfo(List<Task> list) {
        if(!list.isEmpty()){
            list.stream().forEach(task -> {
                System.out.println("------------------------------------------------");
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("------------------------------------------------");
            } );
        }


    }

}
