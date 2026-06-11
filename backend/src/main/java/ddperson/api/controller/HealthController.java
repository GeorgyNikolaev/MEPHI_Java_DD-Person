package ddperson.api.controller;

import ddperson.api.ApiMessages;
import ddperson.api.MessageCode;
import ddperson.api.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final ApiMessages messages;

    public HealthController(ApiMessages messages) {
        this.messages = messages;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", messages.get(MessageCode.SUCCESS_HEALTH_OK));
    }
}
