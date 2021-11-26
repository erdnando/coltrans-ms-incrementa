package com.coltrans.incrementa;

import com.coltrans.models.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class MQConfig {

    public static final String QUEUE = "q_ms_contador";
    public static final String EXCHANGE = "";
    public static final String ROUTING_KEY = "";

    @Bean
    public Queue queue(){
        Queue message_queue = new Queue(QUEUE,false, false, false);
        return message_queue;
    }


    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }

    public Boolean proccessAuxQueue(Connection con, String q_origin, Payload payload) {

        Channel channel = con.createChannel(false);
        Gson gson  = new GsonBuilder().create();

        String message =gson.toJson(payload);
        System.out.println("queueName: "+q_origin);

        try {
            channel.queueDeclare(q_origin, false, false, true, null);
        } catch (IOException e) {
            System.out.println("Tratando de recrear la queue....");
        }

        //------------------------------------------------------------------------------------------------
        //-----------------Send message to dynamic queue--------------------------------------------------
        //------------------------------------------------------------------------------------------------
        try {
            channel.basicPublish("", q_origin, null, message.getBytes());
        } catch (IOException e) {
            // e.printStackTrace();
        }

        System.out.println("[X], sent '"+message+"'");

        //------------------------------------------------------------------------------------------------
        //-----------------Release resources--------------------------------------------------------------
        //------------------------------------------------------------------------------------------------
        try {
            channel.close(0, q_origin);
            con.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return true;
    }



}
