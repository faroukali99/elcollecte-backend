package com.elcollecte.media.controller;

import com.elcollecte.media.dto.MediaDto;
import com.elcollecte.media.dto.UploadResponse;
import com.elcollecte.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medias")
@Tag(name = "Medias", description = "Gestion des fichiers et médias")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Uploader un fichier")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long collecteId,
            @RequestParam(required = false) Long projetId) {
        return ResponseEntity.ok(mediaService.upload(file, userId, collecteId, projetId));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Récupérer un média par UUID")
    public ResponseEntity<MediaDto> findByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(mediaService.findByUuid(uuid));
    }

    @GetMapping
    @Operation(summary = "Lister tous les médias")
    public ResponseEntity<List<MediaDto>> findAll() {
        return ResponseEntity.ok(mediaService.findAll());
    }

    @GetMapping("/collecte/{collecteId}")
    @Operation(summary = "Lister les médias d'une collecte")
    public ResponseEntity<List<MediaDto>> findByCollecteId(@PathVariable Long collecteId) {
        return ResponseEntity.ok(mediaService.findByCollecteId(collecteId));
    }

    @GetMapping("/projet/{projetId}")
    @Operation(summary = "Lister les médias d'un projet")
    public ResponseEntity<List<MediaDto>> findByProjetId(@PathVariable Long projetId) {
        return ResponseEntity.ok(mediaService.findByProjetId(projetId));
    }

    @GetMapping("/mes-medias")
    @Operation(summary = "Lister mes médias")
    public ResponseEntity<List<MediaDto>> findMyMedias(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(mediaService.findByUploadePar(userId));
    }
}
