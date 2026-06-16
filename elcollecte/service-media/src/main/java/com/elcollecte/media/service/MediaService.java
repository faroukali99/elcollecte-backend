package com.elcollecte.media.service;

import com.elcollecte.media.dto.MediaDto;
import com.elcollecte.media.dto.UploadResponse;
import com.elcollecte.media.entity.Media;
import com.elcollecte.media.repository.MediaRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket.name:elcollecte-media}")
    private String bucketName;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    public MediaService(MediaRepository mediaRepository, MinioClient minioClient) {
        this.mediaRepository = mediaRepository;
        this.minioClient = minioClient;
    }

    public UploadResponse upload(MultipartFile file, Long userId, Long collecteId, Long projetId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFilename = UUID.randomUUID() + fileExtension;
            
            // Upload to MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uniqueFilename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            // Save metadata to database
            Media media = new Media();
            media.setNomFichier(uniqueFilename);
            media.setNomOriginal(originalFilename);
            media.setTypeMime(file.getContentType());
            media.setTailleOctets(file.getSize());
            media.setCheminStockage(bucketName + "/" + uniqueFilename);
            media.setUrlAcces(minioEndpoint + "/" + bucketName + "/" + uniqueFilename);
            media.setCollecteId(collecteId);
            media.setProjetId(projetId);
            media.setUploadePar(userId);
            
            media = mediaRepository.save(media);

            return new UploadResponse(
                media.getUuid(),
                media.getUrlAcces(),
                "Fichier uploadé avec succès"
            );

        } catch (MinioException | IOException | InvalidKeyException | 
                 NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }

    public MediaDto findByUuid(UUID uuid) {
        return mediaRepository.findByUuid(uuid)
                .map(MediaDto::from)
                .orElseThrow(() -> new RuntimeException("Media not found"));
    }

    public List<MediaDto> findByCollecteId(Long collecteId) {
        return mediaRepository.findByCollecteId(collecteId).stream()
                .map(MediaDto::from)
                .toList();
    }

    public List<MediaDto> findByProjetId(Long projetId) {
        return mediaRepository.findByProjetId(projetId).stream()
                .map(MediaDto::from)
                .toList();
    }

    public List<MediaDto> findByUploadePar(Long uploadePar) {
        return mediaRepository.findByUploadePar(uploadePar).stream()
                .map(MediaDto::from)
                .toList();
    }

    public List<MediaDto> findAll() {
        return mediaRepository.findAll().stream()
                .map(MediaDto::from)
                .toList();
    }
}
