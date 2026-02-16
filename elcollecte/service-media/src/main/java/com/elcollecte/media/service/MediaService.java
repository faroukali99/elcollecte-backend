package com.elcollecte.media.service;

import com.elcollecte.media.entity.Media;
import com.elcollecte.media.repository.MediaRepository;
import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);

    private final MediaRepository mediaRepo;
    private final MinioClient     minioClient;

    @Value("${spring.minio.bucket:elcollecte-medias}")
    private String bucket;

    public MediaService(MediaRepository mediaRepo, MinioClient minioClient) {
        this.mediaRepo   = mediaRepo;
        this.minioClient = minioClient;
    }

    @Transactional
    public Media upload(MultipartFile file, Long collecteId,
                        Long uploadedBy, String typeMedia) throws Exception {
        // Assurer l'existence du bucket
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        String extension = getExtension(file.getOriginalFilename());
        String objectName = "collectes/" + collecteId + "/" +
                UUID.randomUUID() + "." + extension;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        Media media = new Media();
        media.setCollecteId(collecteId);
        media.setUploadedBy(uploadedBy);
        media.setTypeMedia(Media.TypeMedia.valueOf(
                typeMedia != null ? typeMedia.toUpperCase() : "PHOTO"));
        media.setFilename(objectName);
        media.setOriginalName(file.getOriginalFilename());
        media.setUrlStockage(objectName);
        media.setBucket(bucket);
        media.setTailleBytes(file.getSize());
        media.setMimeType(file.getContentType());

        return mediaRepo.save(media);
    }

    @Transactional(readOnly = true)
    public List<Media> findByCollecte(Long collecteId) {
        return mediaRepo.findByCollecteId(collecteId);
    }

    public String getPresignedUrl(Long mediaId) throws Exception {
        Media media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Média introuvable: " + mediaId));

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .object(media.getUrlStockage())
                .expiry(1, TimeUnit.HOURS)
                .build());
    }

    @Transactional
    public void delete(Long mediaId) throws Exception {
        Media media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Média introuvable: " + mediaId));
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket).object(media.getUrlStockage()).build());
        mediaRepo.delete(media);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}