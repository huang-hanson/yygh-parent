package com.hanson.yygh.task.scheduled;

import com.hanson.common.rabbit.constant.MqConst;
import com.hanson.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hanson
 * @date 2024/5/30 1:37
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    //每天8点执行方法，就医提醒
    //cron表达式，设置执行间隔
    //0 0 8 * * ?
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
