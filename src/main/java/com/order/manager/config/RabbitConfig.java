package com.order.manager.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    public static final String queueA = "queueA_toTopicExchange";
    public static final String queueB = "queueB_toTopicExchange";
    public static final String queueC1 = "queueC1_tofanoutExchange";
    public static final String queueC2 = "queueC2_tofanoutExchange";
    public static final String queueD = "queueD_toDefaultExchange";
    public static final String topicExchange = "topic_exchange";
    public static final String fanoutExchnage = "fanout_exchange";
    public static final String routingKeyA = "topic_exchange_keyA";
    public static final String routingKeyB = "topic_exchange_keyB";

    @Bean
    public Queue queueA() {
        return new Queue(queueA);
    }

    @Bean
    public Queue queueB() {
        return new Queue(queueB);
    }

    @Bean
    public Queue queueC1() {
        return new Queue(queueC1);
    }

    @Bean
    public Queue queueC2() {
        return new Queue(queueC2);
    }

    @Bean
    public Queue queueD() {
        return new Queue(queueD);
    }

    @Bean
    public TopicExchange topicsExchange(){
        return new TopicExchange(topicExchange);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(fanoutExchnage);
    }

    @Bean
    Binding bindingTopicExchange1(){
        final Map<String, Object> arguments = new HashMap<>();
        return new Binding(queueA,Binding.DestinationType.QUEUE,topicExchange,routingKeyA,arguments);
    }

    @Bean
    Binding bindingTopicExchange2(){
        final Map<String, Object> arguments = new HashMap<>();
        return new Binding(queueB,Binding.DestinationType.QUEUE,topicExchange,routingKeyB,arguments);
    }

    @Bean
    Binding bindingTopicExchange3(){
        final Map<String, Object> arguments = new HashMap<>();
        return new Binding(queueC1,Binding.DestinationType.QUEUE,topicExchange,routingKeyB,arguments);
    }

    @Bean
    public Binding bindingFanoutExchnage1(){
        final Map<String, Object> arguments = new HashMap<>();
        return new Binding(queueC1,Binding.DestinationType.QUEUE,fanoutExchnage,"",arguments);
    }

    @Bean
    public Binding bindingFanoutExchnage2(){
        final Map<String, Object> arguments = new HashMap<>();
        return new Binding(queueC2,Binding.DestinationType.QUEUE,fanoutExchnage,"",arguments);
    }

}

