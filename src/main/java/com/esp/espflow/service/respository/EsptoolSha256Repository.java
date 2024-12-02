package com.esp.espflow.service.respository;

import com.esp.espflow.entity.EsptoolSha256Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author rubn
 */
@Repository
public interface EsptoolSha256Repository extends JpaRepository<EsptoolSha256Entity, Long> {

    Optional<EsptoolSha256Entity> findBySha256(final String sha256);

}
