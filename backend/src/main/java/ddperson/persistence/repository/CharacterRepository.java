package ddperson.persistence.repository;

import ddperson.persistence.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID> {

    List<CharacterEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);
}
