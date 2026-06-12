package ddperson.persistence.repository;

import ddperson.persistence.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID> {

    List<CharacterEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Page<CharacterEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId, Pageable pageable);

    Optional<CharacterEntity> findByIdAndUserId(UUID id, UUID userId);
}
