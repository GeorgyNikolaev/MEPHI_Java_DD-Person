package ddperson.api.controller;

import ddperson.api.ApiMessages;
import ddperson.api.MessageCode;
import ddperson.api.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Проверка работоспособности")
public class HealthController {

    private final ApiMessages messages;

    public HealthController(ApiMessages messages) {
        this.messages = messages;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public HealthResponse health() {
        return new HealthResponse("UP", messages.get(MessageCode.SUCCESS_HEALTH_OK));
    }
}
