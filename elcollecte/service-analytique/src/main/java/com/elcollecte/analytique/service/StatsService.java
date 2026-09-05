package com.elcollecte.analytique.service;

import com.elcollecte.analytique.entity.StatsProjet;
import com.elcollecte.analytique.repository.StatsDailyRepository;
import com.elcollecte.analytique.repository.StatsProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final StatsProjetRepository statsRepo;
    private final StatsDailyRepository  dailyRepo;

    public StatsService(StatsProjetRepository statsRepo,
                        StatsDailyRepository dailyRepo) {
        this.statsRepo = statsRepo;
        this.dailyRepo = dailyRepo;
    }

    @Transactional
    public void initProjet(Long projetId) {
        if (!statsRepo.existsByProjetId(projetId)) {
            StatsProjet stats = new StatsProjet();
            stats.setProjetId(projetId);
            statsRepo.save(stats);
        }
    }

    @Transactional
    public void onNouvelleCollecte(Long projetId) {
        StatsProjet stats = getOrCreate(projetId);
        stats.setTotalCollectes(stats.getTotalCollectes() + 1);
        stats.setCollectesEnAttente(stats.getCollectesEnAttente() + 1);
        statsRepo.save(stats);
        dailyRepo.incrementCollectes(projetId, LocalDate.now());
    }

    @Transactional
    public void onCollecteValidee(Long projetId) {
        StatsProjet stats = getOrCreate(projetId);
        stats.setCollectesValidees(stats.getCollectesValidees() + 1);
        stats.setCollectesEnAttente(Math.max(0, stats.getCollectesEnAttente() - 1));
        recalculTaux(stats);
        statsRepo.save(stats);
        dailyRepo.incrementValidees(projetId, LocalDate.now());
    }

    @Transactional
    public void onCollecteRejetee(Long projetId) {
        StatsProjet stats = getOrCreate(projetId);
        stats.setCollectesRejetees(stats.getCollectesRejetees() + 1);
        stats.setCollectesEnAttente(Math.max(0, stats.getCollectesEnAttente() - 1));
        recalculTaux(stats);
        statsRepo.save(stats);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(Long orgId, Long userId, String role) {
        Map<String, Object> dashboard = new HashMap<>();
        List<StatsProjet> allStats = statsRepo.findAll();

        long totalCollectes  = allStats.stream().mapToLong(StatsProjet::getTotalCollectes).sum();
        long totalValidees   = allStats.stream().mapToLong(StatsProjet::getCollectesValidees).sum();
        long totalEnAttente  = allStats.stream().mapToLong(StatsProjet::getCollectesEnAttente).sum();
        long projetsActifs   = allStats.size();

        double tauxMoyen = totalCollectes > 0
            ? (double) totalValidees / totalCollectes * 100 : 0;

        dashboard.put("totalCollectes",   totalCollectes);
        dashboard.put("totalValidees",    totalValidees);
        dashboard.put("totalEnAttente",   totalEnAttente);
        dashboard.put("projetsActifs",    projetsActifs);
        dashboard.put("tauxValidation",   Math.round(tauxMoyen * 100.0) / 100.0);
        dashboard.put("collectesAujourdhui",
            dailyRepo.countByDate(LocalDate.now()));

        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTimeline(Long projetId, int days) {
        LocalDate from = LocalDate.now().minusDays(days);
        return dailyRepo.findTimelineByProjet(projetId, from);
    }

    @Transactional(readOnly = true)
    public StatsProjet getByProjet(Long projetId) {
        return getOrCreate(projetId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getByEnqueteur(Long enqueteurId, Long orgId) {
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: Implementer la logique de statistiques par enquêteur
        // Pour l'instant, retourne des données de placeholder
        stats.put("enqueteurId", enqueteurId);
        stats.put("totalCollectes", 0);
        stats.put("collectesValidees", 0);
        stats.put("collectesRejetees", 0);
        stats.put("collectesEnAttente", 0);
        stats.put("tauxValidation", 0.0);
        
        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getByPeriode(String dateDebut, String dateFin, Long orgId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate debut = LocalDate.parse(dateDebut, formatter);
        LocalDate fin = LocalDate.parse(dateFin, formatter);
        
        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> timeline = dailyRepo.findTimelineByProjet(null, debut);
        
        long totalCollectes = timeline.stream()
            .filter(t -> {
                LocalDate date = LocalDate.parse(t.get("date").toString());
                return !date.isBefore(debut) && !date.isAfter(fin);
            })
            .mapToLong(t -> ((Number) t.getOrDefault("collectes", 0)).longValue())
            .sum();
        
        long totalValidees = timeline.stream()
            .filter(t -> {
                LocalDate date = LocalDate.parse(t.get("date").toString());
                return !date.isBefore(debut) && !date.isAfter(fin);
            })
            .mapToLong(t -> ((Number) t.getOrDefault("validees", 0)).longValue())
            .sum();
        
        long totalRejetees = timeline.stream()
            .filter(t -> {
                LocalDate date = LocalDate.parse(t.get("date").toString());
                return !date.isBefore(debut) && !date.isAfter(fin);
            })
            .mapToLong(t -> ((Number) t.getOrDefault("rejetees", 0)).longValue())
            .sum();
        
        stats.put("periode", dateDebut + " au " + dateFin);
        stats.put("totalCollectes", totalCollectes);
        stats.put("totalValidees", totalValidees);
        stats.put("totalRejetees", totalRejetees);
        stats.put("totalEnAttente", totalCollectes - totalValidees - totalRejetees);
        stats.put("tauxValidation", totalCollectes > 0 ? 
            Math.round((double) totalValidees / totalCollectes * 10000.0) / 100.0 : 0.0);
        
        return stats;
    }

    // ── Private ───────────────────────────────────────────────────────────────
    private StatsProjet getOrCreate(Long projetId) {
        return statsRepo.findByProjetId(projetId)
            .orElseGet(() -> {
                StatsProjet s = new StatsProjet();
                s.setProjetId(projetId);
                return statsRepo.save(s);
            });
    }

    private void recalculTaux(StatsProjet stats) {
        if (stats.getTotalCollectes() > 0) {
            double taux = (double) stats.getCollectesValidees()
                          / stats.getTotalCollectes() * 100;
            stats.setTauxValidation(Math.round(taux * 100.0) / 100.0);
        }
    }
}
