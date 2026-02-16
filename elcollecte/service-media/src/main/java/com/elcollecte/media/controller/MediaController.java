package com.elcollecte.media.controller;

import com.elcollecte.media.entity.Media;
import com.elcollecte.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medias")
@Tag(name = "Médias", description = "Upload et gestion des photos/signatures via MinIO")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader un fichier (photo, signature, document, audio)")
    public ResponseEntity<Media> upload(
            @RequestPart("file")               MultipartFile file,
            @RequestParam                      Long          collecteId,
            @RequestParam(defaultValue = "PHOTO") String     typeMedia,
            @RequestHeader("X-User-Id")        Long          uploadedBy) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.upload(file, collecteId, uploadedBy, typeMedia));
    }

    @GetMapping("/collecte/{collecteId}")
    @Operation(summary = "Lister les médias d'une collecte")
    public ResponseEntity<List<Media>> listByCollecte(@PathVariable Long collecteId) {
        return ResponseEntity.ok(mediaService.findByCollecte(collecteId));
    }

    @GetMapping("/{id}/url")
    @Operation(summary = "Obtenir une URL pré-signée (valide 1h)")
    public ResponseEntity<Map<String, String>> getUrl(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(Map.of("url", mediaService.getPresignedUrl(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un média")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}