package erinyes.echo.gateway;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = "mqttOutChannel")
public interface MqttMsgGateway {
    void push(String message, @Header(MqttHeaders.TOPIC) String filename);
}
