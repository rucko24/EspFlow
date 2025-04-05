package com.esp.espflow.service.respository;

import com.esp.espflow.entity.HexDumpEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author rubn
 */
@Repository
public interface HexDumpRepository extends JpaRepository<HexDumpEntity, Long> {

    @Query("""
            SELECT h FROM HexDumpEntity h
            WHERE (:filterText IS NULL OR :filterText = ''
            OR LOWER(h.offset) LIKE CONCAT('%', LOWER(:filterText), '%')
            OR LOWER(h.ascii) LIKE CONCAT('%', LOWER(:filterText), '%'))
            """)
    Page<HexDumpEntity> findByFilterText(@Param("filterText") String filterText, Pageable pageable);

}
