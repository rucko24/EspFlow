package com.esp.espflow.service.respository;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author rub'n
 */
@Repository
public interface EsptoolExecutableRepository extends JpaRepository<EsptoolExecutableEntity, Long> {

    @Query("SELECT entity " +
            "FROM EsptoolExecutableEntity entity " +
            "WHERE entity.esptoolVersion = :esptoolVersion " +
            "AND entity.isBundled = :isBundled")
    Optional<EsptoolExecutableEntity> findByEsptoolVersionWithBundle(@Param("esptoolVersion")String esptoolVersion,
                                                                     @Param("isBundled") boolean isBundle);

    @Query("SELECT entity " +
            "FROM EsptoolExecutableEntity entity " +
            "WHERE entity.absolutePathEsptool = :absolutePathEsptool " +
            "AND entity.isBundled = :isBundled " +
            "AND entity.esptoolVersion = :esptoolVersion")
    Optional<EsptoolExecutableEntity> findByAbsolutePathEsptoolAndIsBundleAndVersion(@Param("absolutePathEsptool") String absolutePathEsptool,
                                                                                     @Param("isBundled") boolean isBundle,
                                                                                     @Param("esptoolVersion") String esptoolVersion);

    @Query("SELECT entity " +
            "FROM EsptoolExecutableEntity entity " +
            "WHERE entity.isSelected = true")
    Optional<EsptoolExecutableEntity> findByIsSelectedToTrue();

    @Modifying
    @Query("UPDATE EsptoolExecutableEntity entity " +
            "SET entity.isSelected = false " +
            "WHERE entity.id <> :id")
    void updateAllSelectedToFalseExcept(@Param("id") Long id);

}
