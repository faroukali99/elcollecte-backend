package com.elcollecte.analytique.repository;

import com.elcollecte.analytique.entity.StatsDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsDailyRepository extends JpaRepository<StatsDaily, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO stats_daily (projet_id, date_jour, nb_collectes, nb_validees)
        VALUES (:projetId, :date, 1, 0)
        ON CONFLICT (projet_id, date_jour)
        DO UPDATE SET nb_collectes = stats_daily.nb_collectes + 1
        """, nativeQuery = true)
    void incrementCollectes(@Param("projetId") Long projetId, @Param("date") LocalDate date);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO stats_daily (projet_id, date_jour, nb_collectes, nb_validees)
        VALUES (:projetId, :date, 0, 1)
        ON CONFLICT (projet_id, date_jour)
        DO UPDATE SET nb_validees = stats_daily.nb_validees + 1
        """, nativeQuery = true)
    void incrementValidees(@Param("projetId") Long projetId, @Param("date") LocalDate date);

    @Query(value = """
        SELECT COALESCE(SUM(nb_collectes), 0)
        FROM stats_daily
        WHERE date_jour = :date
        """, nativeQuery = true)
    long countByDate(@Param("date") LocalDate date);

    @Query(value = """
        SELECT date_jour AS date, nb_collectes AS collectes, nb_validees AS validees
        FROM stats_daily
        WHERE projet_id = :projetId AND date_jour >= :from
        ORDER BY date_jour
        """, nativeQuery = true)
    List<Map<String, Object>> findTimelineByProjet(@Param("projetId") Long projetId,
                                                    @Param("from") LocalDate from);

    @Query(value = """
        SELECT date_jour AS date,
               SUM(nb_collectes) AS collectes,
               SUM(nb_validees)  AS validees
        FROM stats_daily
        WHERE date_jour >= :from
        GROUP BY date_jour
        ORDER BY date_jour
        """, nativeQuery = true)
    List<Map<String, Object>> findGlobalTimeline(@Param("from") LocalDate from);
}
