package ddperson.persistence.repository;

import ddperson.domain.enums.GenerationStatus;
import ddperson.persistence.entity.GenerationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenerationRequestRepository extends JpaRepository<GenerationRequestEntity, UUID> {

    Optional<GenerationRequestEntity> findByIdAndUserId(UUID id, UUID userId);

    Page<GenerationRequestEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<GenerationRequestEntity> findByUserIdAndStatusOrderByCreatedAtDesc(
            UUID userId, GenerationStatus status, Pageable pageable);

    List<GenerationRequestEntity> findByStatus(GenerationStatus status);

    List<GenerationRequestEntity> findByCharacter_Id(UUID characterId);
}
