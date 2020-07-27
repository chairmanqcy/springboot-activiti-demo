package com.jiuxin.workflow.controller.business;

import com.jiuxin.workflow.entity.ModelEntity;
import com.jiuxin.workflow.entity.ResponseEntity;
import com.jiuxin.workflow.service.DynamicCreateProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "创建流程接口")
@RestController
@RequestMapping("/create")
public class DynamicCreateProcessController {

    @Autowired
    private DynamicCreateProcess dynamicCreateProcess;

    @ApiOperation(value = "创建流程")
    @PostMapping
    ResponseEntity dynamicCreateProcess(@RequestParam String processKey, @RequestBody List<ModelEntity> taskList){
//        processKey = "multiple-process3";
//        List<ModelEntity> taskList = new ArrayList<>();
//        IntStream.range(0,4).forEach(i -> {
//            ModelEntity modelEntity = new ModelEntity();
//            modelEntity.setTaskName("任务"+(i+1));
//            modelEntity.setAssignee("用户"+(i+1));
//            taskList.add(modelEntity);
//        });
        return ResponseEntity.ok(dynamicCreateProcess.dynamicCreateProcess(processKey,taskList));
    }
}
