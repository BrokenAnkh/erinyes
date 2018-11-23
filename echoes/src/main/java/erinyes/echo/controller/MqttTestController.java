package erinyes.echo.controller;


import erinyes.echo.gateway.MqttMsgGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Api(tags = "Mqtt收发测试")
@RestController
@RequestMapping("cici")
public class MqttTestController {

    @Autowired
    private MqttMsgGateway mqttMsgGateway;

    @Autowired
    @Qualifier("mqttInFlow")
    private IntegrationFlow mqttInFlow;

    @ApiOperation(value="发送mqtt消息", notes = "<-.->")
    @PostMapping("/send")
    public Mono<String> sendMessage(
            @RequestParam("message") String message,
            @RequestParam(name="topic",defaultValue = "good",required = false) String topic
    ) {
        mqttMsgGateway.push(message,topic);
        return Mono.just("OK");
    }

    @ApiOperation(value="接收mqtt消息", notes = "|=_=|")
    @GetMapping("/receive")
    public String receiveMessage() {
        return "OK";
    }
}
