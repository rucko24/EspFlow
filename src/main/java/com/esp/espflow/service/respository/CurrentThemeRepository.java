package com.esp.espflow.service.respository;

import com.esp.espflow.entity.CurrentThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author rub'n
 */
@Repository
public interface CurrentThemeRepository extends JpaRepository<CurrentThemeEntity, Long> {

    @Query("""
            SELECT entity
            FROM CurrentThemeEntity entity
            WHERE entity.isEnabled = true
            """)
    Optional<CurrentThemeEntity> findActiveTheme();

}
