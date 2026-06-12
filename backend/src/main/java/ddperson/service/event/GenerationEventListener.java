package ddperson.service.event;

import ddperson.generation.pipeline.ImageGenerationPipeline;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class GenerationEventListener {

    private final ImageGenerationPipeline pipeline;

    public GenerationEventListener(ImageGenerationPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Async("generationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGenerationRequested(GenerationRequestedEvent event) {
        pipeline.execute(event.requestId());
    }
}
