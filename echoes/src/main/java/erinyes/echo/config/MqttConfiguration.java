package erinyes.echo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
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
public class MqttConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntergrationConfiguration.class);

    @Value("${mqtt.broker.urls}")
    String[] urls;
    @Value("${mqtt.broker.username}")
    String username;
    @Value("${mqtt.broker.password}")
    String password;
    @Value("${mqtt.client.keepalive}")
    int keepAlive;
    @Value("${mqtt.client.connectionTimeout}")
    int connectionTimeout;

    @Bean("mqttConnectOptions")
    public MqttConnectOptions getOptions() {
        LOGGER.info("options load");
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(urls);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setKeepAliveInterval(keepAlive);
        options.setConnectionTimeout(connectionTimeout);

        return options;
    }

    @Bean("mqttClientFactory")
    public MqttPahoClientFactory getFactory() {
        LOGGER.info("factory load");
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getOptions());
        return factory;
    }

    @Configuration
    public class ConsumerConfiguration {
        @Value("${mqtt.client.qos}")
        int qos;
        @Value("${mqtt.client.completionTimeout}")
        int completionTimeout;
        @Value("${mqtt.consumer.id}")
        String consumerId;
        @Value("${mqtt.consumer.topics}")
        String[] consumerTopics;

        @Bean("mqttInFlow")
        public IntegrationFlow getFlow() {
            return IntegrationFlows
                    .from(getAdapter())
                    .transform(p -> p + ", received from MQTT")
                    .handle(getHandler())
                    .get();
        }

        @Bean("mqttLoggingHandler")
        public LoggingHandler getHandler() {
            LoggingHandler loggingHandler = new LoggingHandler("INFO");
            loggingHandler.setLoggerName("Consume");
            return loggingHandler;
        }

        @Bean("mqttInbound")
        public MessageProducerSupport getAdapter() {
            MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(consumerId, getFactory(), consumerTopics);
            adapter.setCompletionTimeout(completionTimeout);
            adapter.setConverter(new DefaultPahoMessageConverter());
            adapter.setQos(qos);
//            adapter.setOutputChannel(getChannel());
            return adapter;
        }

//        @Bean("mqttInChannel")
//        public MessageChannel getChannel() {
//            return new DirectChannel();
//        }
    }

    @Configuration
    public class ProducerConfiguration {
        @Value("${mqtt.producer.id}")
        String producerId;
        @Value("${mqtt.producer.default.topic}")
        String producerTopic;

        @Bean("mqttOutFlow")
        public IntegrationFlow getFlow() {
            return IntegrationFlows
                    .from(getChannel())
                    .transform(p -> p + " sent to MQTT")
                    .handle(getHandler())
                    .get();
        }

        @Bean("mqttOutChannel")
        public MessageChannel getChannel() {
            return new DirectChannel();
        }

        @Bean("mqttOutbound")
        public MessageHandler getHandler() {
            MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(producerId, getFactory());
            messageHandler.setAsync(true);
            messageHandler.setDefaultTopic(producerTopic);
            return messageHandler;
        }
    }
}
