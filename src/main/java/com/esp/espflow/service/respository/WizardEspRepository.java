package com.esp.espflow.service.respository;

import com.esp.espflow.entity.WizardEspEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author rubn
 */
@Repository
public interface WizardEspRepository extends JpaRepository<WizardEspEntity, Long> {

    Optional<WizardEspEntity> findByName(String name);

    @Query("""
            SELECT COUNT(isWizardEnabled)
            FROM WizardEspEntity
            WHERE isWizardEnabled = true
            """)
    Integer areAllWizardsEnabled();
}
