package com.coltrans.incrementa;

import com.coltrans.models.Payload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IncrementaListener {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ConnectionFactory cf;

    @Autowired
    private MQConfig engine;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(Payload payload){

        System.out.println(payload);

        //------------------------------------------------------------------------------------------------
        //-----------------Read parameters----------------------------------------------------------------
        //------------------------------------------------------------------------------------------------
        //Get origin queue
        String q_origin = payload.getQ_origin();
        String sContador = payload.getValor();
        String q_action = payload.getAccion();

        //------------------------------------------------------------------------------------------------
        //-----------------Local logic--------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------
        int operando = (q_action.equals("add")  ? 1: -1);
        int intContador = Integer.parseInt(sContador)+ operando;

        //------------------------------------------------------------------------------------------------
        //-----------------Prepare response --------------------------------------------------------------
        //------------------------------------------------------------------------------------------------
        payload.setValor(String.valueOf(intContador));

        //------------------------------------------------------------------------------------------------
        //-----------------Connect & create & send message to dynamic queue-------------------------------
        //------------------------------------------------------------------------------------------------
        engine.proccessAuxQueue(cf.createConnection(),q_origin,payload);

    }
}
