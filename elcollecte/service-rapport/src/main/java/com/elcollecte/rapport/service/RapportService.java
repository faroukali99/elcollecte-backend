package com.elcollecte.rapport.service;

import com.elcollecte.rapport.dto.CreateRapportRequest;
import com.elcollecte.rapport.dto.RapportDto;
import com.elcollecte.rapport.entity.Rapport;
import com.elcollecte.rapport.repository.RapportRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RapportService {

    private final RapportRepository rapportRepository;

    public RapportService(RapportRepository rapportRepository) {
        this.rapportRepository = rapportRepository;
    }

    public RapportDto create(CreateRapportRequest request, Long userId) {
        Rapport rapport = new Rapport();
        rapport.setProjetId(request.projetId());
        rapport.setFormulaireId(request.formulaireId());
        rapport.setType(request.type());
        rapport.setTitre(request.titre());
        rapport.setFormat(request.format());
        rapport.setParametres(request.parametres());
        rapport.setCreePar(userId);
        rapport.setStatut("EN_ATTENTE");
        
        rapport = rapportRepository.save(rapport);
        
        // Generate report asynchronously
        generateRapport(rapport);
        
        return RapportDto.from(rapport);
    }

    public RapportDto findByUuid(UUID uuid) {
        return rapportRepository.findByUuid(uuid)
                .map(RapportDto::from)
                .orElseThrow(() -> new RuntimeException("Rapport not found"));
    }

    public List<RapportDto> findByProjetId(Long projetId) {
        return rapportRepository.findByProjetId(projetId).stream()
                .map(RapportDto::from)
                .toList();
    }

    public List<RapportDto> findByCreePar(Long creePar) {
        return rapportRepository.findByCreePar(creePar).stream()
                .map(RapportDto::from)
                .toList();
    }

    public List<RapportDto> findAll() {
        return rapportRepository.findAll().stream()
                .map(RapportDto::from)
                .toList();
    }

    private void generateRapport(Rapport rapport) {
        try {
            rapport.setStatut("EN_COURS");
            rapportRepository.save(rapport);

            // Generate PDF report
            byte[] pdfBytes = generatePdf(rapport);
            
            // In a real implementation, you would save to MinIO or file system
            // For now, we'll simulate the path
            String cheminFichier = "/rapports/" + rapport.getUuid() + ".pdf";
            rapport.setCheminFichier(cheminFichier);
            rapport.setStatut("GENERE");
            rapport.setGenereAt(LocalDateTime.now());
            rapportRepository.save(rapport);
            
        } catch (Exception e) {
            rapport.setStatut("ERREUR");
            rapport.setErreurMessage(e.getMessage());
            rapportRepository.save(rapport);
        }
    }

    private byte[] generatePdf(Rapport rapport) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph("Rapport: " + rapport.getTitre()));
        document.add(new Paragraph("Type: " + rapport.getType()));
        document.add(new Paragraph("Format: " + rapport.getFormat()));
        document.add(new Paragraph("Généré le: " + LocalDateTime.now()));
        
        document.close();
        return baos.toByteArray();
    }
}
