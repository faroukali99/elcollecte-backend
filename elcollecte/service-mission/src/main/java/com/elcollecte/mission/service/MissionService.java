package com.elcollecte.mission.service;

import com.elcollecte.mission.dto.CreateMissionRequest;
import com.elcollecte.mission.dto.MissionDto;
import com.elcollecte.mission.dto.UpdateMissionRequest;
import com.elcollecte.mission.entity.Mission;
import com.elcollecte.mission.entity.MissionEnqueteur;
import com.elcollecte.mission.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Transactional
    public MissionDto create(CreateMissionRequest req) {
        Mission mission = new Mission();
        mission.setProjetId(req.projetId());
        mission.setFormulaireId(req.formulaireId());
        mission.setFormulaireVersionId(req.formulaireVersionId());
        mission.setZoneId(req.zoneId());
        mission.setTitre(req.titre());
        mission.setDescription(req.description());
        mission.setObjectif(req.objectif());
        mission.setDateDebut(req.dateDebut());
        mission.setDateFin(req.dateFin());
        mission.setStatut(Mission.Statut.PLANIFIEE);

        return MissionDto.from(missionRepository.save(mission));
    }

    @Transactional(readOnly = true)
    public List<MissionDto> findByProjet(Long projetId) {
        return missionRepository.findByProjetId(projetId).stream()
            .map(MissionDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public MissionDto findById(Long id) {
        return missionRepository.findById(id)
            .map(MissionDto::from)
            .orElseThrow(() -> new NoSuchElementException("Mission introuvable: " + id));
    }

    @Transactional
    public MissionDto update(Long id, UpdateMissionRequest req) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Mission introuvable: " + id));

        if (req.titre() != null) mission.setTitre(req.titre());
        if (req.description() != null) mission.setDescription(req.description());
        if (req.objectif() != null) mission.setObjectif(req.objectif());
        if (req.dateDebut() != null) mission.setDateDebut(req.dateDebut());
        if (req.dateFin() != null) mission.setDateFin(req.dateFin());
        if (req.statut() != null) mission.setStatut(Mission.Statut.valueOf(req.statut()));

        return MissionDto.from(missionRepository.save(mission));
    }

    @Transactional
    public void addEnqueteur(Long missionId, Long enqueteurId) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new NoSuchElementException("Mission introuvable"));

        boolean alreadyAssigned = mission.getEnqueteurs().stream()
            .anyMatch(e -> e.getEnqueteurId().equals(enqueteurId) && e.isActive());

        if (!alreadyAssigned) {
            mission.getEnqueteurs().add(new MissionEnqueteur(mission, enqueteurId));
            missionRepository.save(mission);
        }
    }

    @Transactional
    public void removeEnqueteur(Long missionId, Long enqueteurId) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new NoSuchElementException("Mission introuvable"));

        mission.getEnqueteurs().stream()
            .filter(e -> e.getEnqueteurId().equals(enqueteurId))
            .forEach(e -> e.setActive(false));

        missionRepository.save(mission);
    }
}
