package erinyes.echo.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@MessagingGateway
public interface MqttMsgGateway {
    @Gateway(requestChannel = "mqttOutChannel")
    void push(@Payload String message, @Header(MqttHeaders.TOPIC) String filename);

    @Payload("new java.util.Date()")
    List<String> retrieveOpenOrders();
}
