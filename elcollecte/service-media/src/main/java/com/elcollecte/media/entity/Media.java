package com.elcollecte.media.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medias")
public class Media {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collecte_id",  nullable = false) private Long   collecteId;
    @Column(name = "uploaded_by",  nullable = false) private Long   uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_media",   nullable = false) private TypeMedia typeMedia;

    @Column(nullable = false, length = 255)          private String filename;
    @Column(name = "original_name", length = 255)    private String originalName;
    @Column(name = "url_stockage",  nullable = false) private String urlStockage;
    @Column(length = 100)                             private String bucket;
    @Column(name = "taille_bytes")                   private Long   tailleBytes;
    @Column(name = "mime_type", length = 100)        private String mimeType;
    @Column(name = "created_at", updatable = false)  private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum TypeMedia { PHOTO, SIGNATURE, DOCUMENT, AUDIO }

    public Media() {}

    public Long      getId()          { return id; }
    public Long      getCollecteId()  { return collecteId; }
    public Long      getUploadedBy()  { return uploadedBy; }
    public TypeMedia getTypeMedia()   { return typeMedia; }
    public String    getFilename()    { return filename; }
    public String    getOriginalName(){ return originalName; }
    public String    getUrlStockage() { return urlStockage; }
    public String    getBucket()      { return bucket; }
    public Long      getTailleBytes() { return tailleBytes; }
    public String    getMimeType()    { return mimeType; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    public void setCollecteId(Long v)   { this.collecteId = v; }
    public void setUploadedBy(Long v)   { this.uploadedBy = v; }
    public void setTypeMedia(TypeMedia v){ this.typeMedia = v; }
    public void setFilename(String v)   { this.filename = v; }
    public void setOriginalName(String v){ this.originalName = v; }
    public void setUrlStockage(String v){ this.urlStockage = v; }
    public void setBucket(String v)     { this.bucket = v; }
    public void setTailleBytes(Long v)  { this.tailleBytes = v; }
    public void setMimeType(String v)   { this.mimeType = v; }
}