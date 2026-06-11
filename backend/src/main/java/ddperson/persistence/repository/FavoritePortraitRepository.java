package ddperson.persistence.repository;

import ddperson.persistence.entity.FavoritePortraitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoritePortraitRepository extends JpaRepository<FavoritePortraitEntity, UUID> {

    List<FavoritePortraitEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<FavoritePortraitEntity> findByUserIdAndPortraitId(UUID userId, UUID portraitId);

    boolean existsByUserIdAndPortraitId(UUID userId, UUID portraitId);
}
