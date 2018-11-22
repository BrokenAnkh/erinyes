package erinyes.echo.controller;


import erinyes.echo.gateway.MqttMsgGateway;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "Mqtt收发测试")
@RestController
@RequestMapping("cici")
public class MqttTestController {

    @Autowired
    private MqttMsgGateway mqttMsgGateway;

    @ApiOperation(value="发送mqtt消息", notes = "<-.->")
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam("message") String message,
            @RequestParam(name="topic",defaultValue = "good",required = false) String topic
    ) {
        mqttMsgGateway.push(message,topic);
        return "OK";
    }

    @ApiOperation(value="接收mqtt消息", notes = "|=_=|")
    @GetMapping("/receive")
    public String receiveMessage() {
        return "OK";
    }
}
