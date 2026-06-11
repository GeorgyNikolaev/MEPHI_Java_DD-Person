package ddperson.persistence.repository;

import ddperson.persistence.entity.PortraitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortraitRepository extends JpaRepository<PortraitEntity, UUID> {
}
