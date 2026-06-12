package ddperson.persistence.repository;

import ddperson.persistence.entity.GigachatApiCallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.UUID;

public interface GigachatApiCallRepository extends JpaRepository<GigachatApiCallEntity, UUID> {

    @Modifying
    void deleteByRequestId(UUID requestId);
}
