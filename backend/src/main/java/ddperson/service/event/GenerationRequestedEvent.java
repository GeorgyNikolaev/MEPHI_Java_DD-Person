package ddperson.service.event;

import java.util.UUID;

public record GenerationRequestedEvent(UUID requestId) {
}
