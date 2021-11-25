package com.coltrans.incrementa;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class MessageListener {

    @Autowired
    private RabbitTemplate template;
    @Autowired
    private ConnectionFactory cf;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(Payload payload){
        //Get the message
        System.out.println(payload);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Read parameters
        String sContador = payload.getValor();
        final String q_origin = payload.getQ_origin();
        final String q_action = payload.getAccion();
        int operando = (q_action.equals("add")  ? 1: -1);
        //Apply local logic
        int intContador = Integer.parseInt(sContador)+ operando;
        payload.setValor(String.valueOf(intContador));


       //---------------------------------------------------------------
       //Publish response to origin queue
        Connection con = cf.createConnection();
        Channel channel = con.createChannel(false);
        Gson gson  = new GsonBuilder().create();

        String queueName = q_origin;
        String message =gson.toJson(payload);
        System.out.println("queueName: "+queueName);

        try {
            // channel.queueDeclarePassive(q_origin);
            channel.queueDeclare(q_origin, false, false, true, null);
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Tratando de recrear la queue....");
        }

        try {
            channel.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
           // e.printStackTrace();
        }



         System.out.println("[X], sent '"+message+"'");

        try {
            channel.close(0, queueName);
            con.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }





    }
}
