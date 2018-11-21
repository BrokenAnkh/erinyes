package erinyes.echo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class IntergrationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntergrationConfiguration.class);

    @Value("${mqtt.broker.urls}")
    String[] urls;
    @Value("${mqtt.broker.username}")
    String username;
    @Value("${mqtt.broker.password}")
    String password;
    @Value("${mqtt.client.keepalive}")
    int keepAlive;
    @Value("${mqtt.client.qos}")
    int qos;
    @Value("${mqtt.client.completiontimeout}")
    int completionTimeout;
    @Value("${mqtt.client.connectiontimeout}")
    int connectionTimeout;
    @Value("${mqtt.consumer.id}")
    String consumerId;
    @Value("${mqtt.consumer.topics}")
    String[] consumerTopics;
    @Value("${mqtt.producer.id}")
    String producerId;
    @Value("${mqtt.producer.default.topic}")
    String producerTopic;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(urls);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setKeepAliveInterval(keepAlive);
        options.setConnectionTimeout(connectionTimeout);

        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        LOGGER.info("make mqtt client");
        return factory;
    }

    //    Cosumer
    @Bean
    public IntegrationFlow mqttInFlow() {
        LOGGER.info("make mqtt in flow");
        return IntegrationFlows
                .from(mqttInbound())
                .transform(p -> p + ", received from MQTT")
                .handle(consumerLogger())
                .get();
    }

    private LoggingHandler consumerLogger() {
        LoggingHandler loggingHandler = new LoggingHandler("INFO");
        loggingHandler.setLoggerName("Consume");
        return loggingHandler;
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(consumerId, mqttClientFactory(), consumerTopics);
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        return adapter;
    }

    //    Producer
    @Bean
    public IntegrationFlow mqttOutFlow() {
        LOGGER.info("make mqtt out flow");
        return IntegrationFlows
                .from(mqttOutChannel())
                .transform(p -> p + " sent to MQTT")
                .handle(mqttOutbound())
                .get();

    }

    @Bean
    public MessageChannel mqttOutChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(producerId, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(producerTopic);
        return messageHandler;
    }
}
