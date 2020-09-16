package com.order.manager;

import com.order.manager.service.Sender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HelloApplicationTests {

    @Autowired
    private Sender sender;

    @Test
    public void hello() throws Exception {
        for(int i = 0; i <=10; i++) {
            sender.sendInDefaultExchange(i);
        }
    }

    @Test
    public void testTopicExchange(){
        sender.sendInTopicExchange();
    }
    @Test
    public void testFanoutExchange(){
        sender.sendInfanoutExchange();
    }



}
