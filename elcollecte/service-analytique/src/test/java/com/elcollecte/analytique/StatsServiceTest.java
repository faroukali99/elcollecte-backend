package com.elcollecte.analytique;

import com.elcollecte.analytique.entity.StatsProjet;
import com.elcollecte.analytique.repository.StatsDailyRepository;
import com.elcollecte.analytique.repository.StatsProjetRepository;
import com.elcollecte.analytique.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private StatsProjetRepository statsRepo;

    @Mock
    private StatsDailyRepository dailyRepo;

    @InjectMocks
    private StatsService statsService;

    private StatsProjet statsProjet;

    @BeforeEach
    void setUp() {
        statsProjet = new StatsProjet();
        statsProjet.setProjetId(1L);
        statsProjet.setTotalCollectes(10L);
        statsProjet.setCollectesValidees(5L);
        statsProjet.setCollectesRejetees(2L);
        statsProjet.setCollectesEnAttente(3L);
    }

    @Test
    void onNouvelleCollecte_shouldIncrementCounters() {
        when(statsRepo.findByProjetId(1L)).thenReturn(Optional.of(statsProjet));

        statsService.onNouvelleCollecte(1L);

        assertEquals(11, statsProjet.getTotalCollectes());
        assertEquals(4, statsProjet.getCollectesEnAttente());
        verify(statsRepo, times(1)).save(statsProjet);
        verify(dailyRepo, times(1)).incrementCollectes(any(), any());
    }

    @Test
    void onCollecteValidee_shouldUpdateCountersAndRate() {
        when(statsRepo.findByProjetId(1L)).thenReturn(Optional.of(statsProjet));

        statsService.onCollecteValidee(1L);

        assertEquals(6, statsProjet.getCollectesValidees());
        assertEquals(2, statsProjet.getCollectesEnAttente());
        assertEquals(54.55, statsProjet.getTauxValidation()); // (6 / 11) * 100
        verify(statsRepo, times(1)).save(statsProjet);
        verify(dailyRepo, times(1)).incrementValidees(any(), any());
    }

    @Test
    void onCollecteRejetee_shouldUpdateCounters() {
        when(statsRepo.findByProjetId(1L)).thenReturn(Optional.of(statsProjet));

        statsService.onCollecteRejetee(1L);

        assertEquals(3, statsProjet.getCollectesRejetees());
        assertEquals(2, statsProjet.getCollectesEnAttente());
        verify(statsRepo, times(1)).save(statsProjet);
    }
}
