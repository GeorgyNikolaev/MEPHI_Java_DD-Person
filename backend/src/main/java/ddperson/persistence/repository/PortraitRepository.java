package ddperson.persistence.repository;

import ddperson.persistence.entity.PortraitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PortraitRepository extends JpaRepository<PortraitEntity, UUID> {

    @Query("SELECT p FROM PortraitEntity p WHERE p.id = :id AND p.request.user.id = :userId")
    Optional<PortraitEntity> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
