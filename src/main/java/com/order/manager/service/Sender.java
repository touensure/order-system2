package com.order.manager.service;

import com.order.manager.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Sender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendInDefaultExchange(int i) {
        String context = "hello queueD" + i + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend(RabbitConfig.queueD, context);
    }

    public void sendInTopicExchange(){
        String contextA = "hello queueA" + new Date();
        String contextB = "hello queueB" + new Date();
        System.out.println("SendInTopicExchange: " + contextA);
        System.out.println("SendInTopicExchange: " + contextB);
        this.rabbitTemplate.convertAndSend(RabbitConfig.topicExchange,RabbitConfig.routingKeyA,contextA);
        this.rabbitTemplate.convertAndSend(RabbitConfig.topicExchange,RabbitConfig.routingKeyB,contextB);
//        this.rabbitTemplate.convertAndSend(RabbitConfig.topicExchange,RabbitConfig.routingKeyA,"like");
//        this.rabbitTemplate.convertAndSend(RabbitConfig.topicExchange,RabbitConfig.routingKeyB,"again");
    }

    public void sendInfanoutExchange(){
        String contextC1 = "hello queueC1" + new Date();
        String contextC2 = "hello queueC2" + new Date();
        System.out.println("SendInFanoutExchange: " + contextC1);
        System.out.println("SendInFanoutExchange: " + contextC2);
        this.rabbitTemplate.convertAndSend(RabbitConfig.fanoutExchnage,"",contextC1);
//        this.rabbitTemplate.convertAndSend(RabbitConfig.fanoutExchnage,"",contextC2);
    }


}

