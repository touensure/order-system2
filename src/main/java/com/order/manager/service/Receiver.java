package com.order.manager.service;

import com.order.manager.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @RabbitListener(queues = RabbitConfig.queueD)
    @RabbitHandler
    public void processQueueD(String hello) {
        System.out.println("ReceiverQueueD : " + hello);
    }

    @RabbitListener(queues = RabbitConfig.queueD)
    @RabbitHandler
    public void processQueueD2(String hello) {
        System.out.println("ReceiverQueueD 2: " + hello);
    }

    @RabbitListener(queues = RabbitConfig.queueA)
    @RabbitHandler
    public void processQueueA(String hello) {
        System.out.println("ReceiverQueueA : " + hello);
    }

    @RabbitListener(queues = RabbitConfig.queueB)
    @RabbitHandler
    public void processQueueB(String hello) {
        System.out.println("ReceiverQueueB : " + hello);
    }

    @RabbitListener(queues = RabbitConfig.queueC1)
    @RabbitHandler
    public void processQueueC1(String hello) {
        System.out.println("ReceiverQueueC1 : " + hello);
    }

    @RabbitListener(queues = RabbitConfig.queueC2)
    @RabbitHandler
    public void processQueueC2(String hello) {
        System.out.println("ReceiverQueueC2 : " + hello);
    }


}
