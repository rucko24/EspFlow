package com.esp.espflow.service.respository;

import com.esp.espflow.entity.EsptoolBundleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EsptoolBundleRepository extends JpaRepository<EsptoolBundleEntity, Long> {

    Optional<EsptoolBundleEntity> findByIsInBundleMode(boolean name);

}
