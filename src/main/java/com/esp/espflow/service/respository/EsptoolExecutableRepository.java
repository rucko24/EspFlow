package com.esp.espflow.service.respository;

import com.esp.espflow.entity.EsptoolExecutableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EsptoolExecutableRepository extends JpaRepository<EsptoolExecutableEntity, Long> {

    @Query("SELECT entity " +
            "FROM EsptoolExecutableEntity entity " +
            "WHERE entity.esptoolVersion = :esptoolVersion " +
            "AND entity.isBundle = :isBundle")
    Optional<EsptoolExecutableEntity> findByEsptoolVersionWithBundle(@Param("esptoolVersion")String esptoolVersion,
                                                                     @Param("isBundle") boolean isBundle);

    @Query("SELECT entity " +
            "FROM EsptoolExecutableEntity entity " +
            "WHERE entity.absolutePathEsptool = :absolutePathEsptool " +
            "AND entity.isBundle = :isBundle " +
            "AND entity.esptoolVersion = ''")
    Optional<EsptoolExecutableEntity> findByAbsolutePathEsptoolAndIsBundle(@Param("absolutePathEsptool") String absolutePathEsptool,
                                                                           @Param("isBundle") boolean isBundle);

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
