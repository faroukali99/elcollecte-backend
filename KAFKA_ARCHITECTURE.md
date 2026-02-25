# 📨 Architecture Kafka - ElCollecte

## Vue d'ensemble

Kafka est un **système de messagerie distribué (event streaming platform)** qui joue un rôle **CENTRAL** dans votre architecture de microservices. Il assure la **communication asynchrone** entre les services en temps réel.

---

## 🎯 Rôle principal de Kafka

### Sans Kafka (risques) ❌
- Les services devraient faire des appels HTTP synchrones (lent)
- Si un service est down, tout s'écroule (couplage fort)
- Pas d'historique des événements
- Impossible de rejouer les événements

### Avec Kafka (avantages) ✅
- **Communication asynchrone** : les services ne se bloquent pas
- **Découplage** : les services ne connaissent pas les détails les uns des autres
- **Fiabilité** : les messages sont persistés et replayables
- **Scalabilité** : plusieurs consommateurs peuvent traiter les mêmes événements
- **Historique immuable** : tous les événements restent stockés

---

## 📊 Architecture du flux d'événements

```
┌─────────────────────────────────────────────────────────────────┐
│                      KAFKA BROKER                               │
│               (Gestionnaire de messages centralisé)              │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ audit.events │  │collecte.*     │  │formulaire.   │          │
│  │ (Topic)      │  │(Topics)       │  │publie(Topic) │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
        ▲                   ▲                   ▲
        │ PRODUCE           │ PRODUCE           │ PRODUCE
        │                   │                   │
┌───────┴─────┐  ┌─────────┴────────┐  ┌──────┴──────────┐
│ Tous         │  │ service-collecte │  │ service-formulaire
│ les services │  │ service-projet   │  │
└───────┬─────┘  └─────────┬────────┘  └──────┬──────────┘
        │                  │                   │
        └──────────────────┼───────────────────┘
                           │
                    ▼      ▼      ▼
        CONSUME (SUBSCRIBERS)
        
    ┌──────────────────┬──────────────────┬──────────────────┐
    │ service-audit    │service-analytique│ (futures)        │
    │ (Log immuable)   │ (Statistiques)    │                  │
    └──────────────────┴──────────────────┴──────────────────┘
```

---

## 📌 Topics Kafka détaillés

### 1️⃣ **audit.events** (AUDIT LOG)
```
Producteur  : TOUS LES SERVICES
Consommateur: service-audit (Port 8089)
Contenu     : Tous les événements importants du système
Événements  :
  - Connexion/Déconnexion utilisateur
  - Création/Modification de projets
  - Soumission de collectes
  - Publication de formulaires
  - Validation/Rejet
  
But         : Traçabilité immuable + Conformité réglementaire
Persistence : Événements JAMAIS supprimés (journal immuable)
```

### 2️⃣ **collecte.soumise**
```
Producteur  : service-collecte
Consommateurs: 
  - service-analytique (mise à jour des stats)
  - service-audit (traçabilité)
Contenu     : Quand une collecte est soumise par un agent
Données     :
  {
    "collecteId": 123,
    "projetId": 1,
    "enqueteurId": 5,
    "timestamp": "2026-02-24T10:30:00",
    "latitude": 12.34,
    "longitude": 56.78,
    "donnees": {...}
  }
```

### 3️⃣ **collecte.validee**
```
Producteur  : service-collecte (après validation)
Consommateurs: 
  - service-analytique (mise à jour des statistiques)
  - service-audit (journal)
Contenu     : Quand une collecte est approuvée par un validateur
```

### 4️⃣ **collecte.rejetee**
```
Producteur  : service-collecte (après rejet)
Consommateurs: 
  - service-analytique (mise à jour stats rejet)
  - service-audit (motif du rejet)
Contenu     : Quand une collecte est rejetée (motif du rejet inclus)
```

### 5️⃣ **projet.cree**
```
Producteur  : service-projet
Consommateurs: 
  - service-analytique (nouveau projet créé)
  - service-audit (traçabilité)
Contenu     : Création d'un nouveau projet
```

### 6️⃣ **formulaire.publie**
```
Producteur  : service-formulaire
Consommateurs: 
  - service-audit (journal)
Contenu     : Quand un formulaire est publié
```

---

## 🔄 Flux d'une Collecte (avec Kafka)

```
1. Agent soumet une collecte via APP MOBILE
                ↓
2. service-collecte reçoit les données
                ↓
3. service-collecte PRODUIT l'événement
   ├─→ Kafka Topic: "collecte.soumise"
                ↓
4. Kafka BROADCAST l'événement à:
   ├─→ service-analytique (écoute "collecte.soumise")
   │   └─→ Met à jour les statistiques en TEMPS RÉEL
   │
   └─→ service-audit (écoute "collecte.soumise")
       └─→ Enregistre dans le journal immuable

5. Admin valide/rejette la collecte dans service-collecte
                ↓
6. service-collecte PRODUIT:
   ├─→ "collecte.validee" OU "collecte.rejetee"
                ↓
7. Kafka BROADCAST à nouveau:
   ├─→ service-analytique (met à jour stats)
   └─→ service-audit (enregistre validation/rejet)

8. Dashboard affiche les stats en TEMPS RÉEL
   (alimentées par service-analytique)
```

---

## ⚙️ Configuration Kafka dans votre projet

### 📝 application.yml (exemple service-collecte)
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      
    consumer:
      bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
      group-id: service-collecte
      auto-offset-reset: earliest  # Lire depuis le début si pas d'offset
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.elcollecte.*"
```

### 🚀 Docker Compose
```yaml
KAFKA_SERVERS: kafka:29092  # Adresse du broker Kafka
```

---

## 💾 Exemples de code (Production d'événements)

### Service-Projet (Producteur)
```java
@Service
public class ProjetService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public Projet create(Projet p) {
        Projet saved = projetRepository.save(p);
        
        // PRODUIT l'événement
        Map<String, Object> event = new HashMap<>();
        event.put("projetId", saved.getId());
        event.put("nom", saved.getNom());
        event.put("timestamp", LocalDateTime.now());
        
        kafkaTemplate.send("projet.cree", String.valueOf(saved.getId()), event);
        
        return saved;
    }
}
```

### Service-Analytique (Consommateur)
```java
@Component
public class CollecteEventConsumer {
    
    @KafkaListener(topics = "projet.cree", groupId = "analytique-service")
    public void onProjetCree(Map<String, Object> event) {
        Long projetId = toLong(event.get("projetId"));
        
        // Mise à jour des statistiques EN TEMPS RÉEL
        StatsProjet stats = new StatsProjet();
        stats.setProjetId(projetId);
        stats.setNomProjet((String) event.get("nom"));
        stats.setDateCreation(LocalDateTime.now());
        
        statsRepository.save(stats);
        log.info("📊 Statistiques mises à jour pour projet: {}", projetId);
    }
    
    @KafkaListener(topics = "collecte.soumise", groupId = "analytique-service")
    public void onCollecteSoumise(Map<String, Object> event) {
        // Incrémenter compteur soumises
        Long projetId = toLong(event.get("projetId"));
        StatsProjet stats = statsRepository.findById(projetId).orElse(new StatsProjet());
        stats.setCollectesSoumises(stats.getCollectesSoumises() + 1);
        statsRepository.save(stats);
    }
}
```

### Service-Audit (Consommateur)
```java
@Component
public class AuditEventConsumer {
    
    @KafkaListener(topics = "audit.events", groupId = "audit-service")
    public void onAuditEvent(Map<String, Object> event) {
        AuditLog log = new AuditLog();
        log.setEventType((String) event.get("eventType"));
        log.setEntityId(toLong(event.get("entityId")));
        log.setUserId(toLong(event.get("userId")));
        log.setTimestamp(LocalDateTime.now());
        
        auditRepository.save(log);
        // Les événements RESTENT STOCKÉS (immuable)
    }
}
```

---

## 🎯 Avantages pour votre système ElCollecte

| Aspect | Bénéfice Kafka |
|--------|---|
| **Temps réel** | Dashboard met à jour les stats EN DIRECT |
| **Audit** | Journal immuable de TOUS les événements |
| **Fiabilité** | Si service-analytique down, events sont bufferisés |
| **Scalabilité** | Ajouter un nouveau service ? Juste écouter un topic ! |
| **Traçabilité** | Qui a validé ? Quand ? Pourquoi ? → ENREGISTRÉ |
| **Conformité** | Trace immuable pour la réglementation |
| **Performance** | Communication asynchrone (pas de blocage) |

---

## 🐛 Problèmes courants et solutions

### ❌ Kafka pas accessible ?
```
Error: java.net.ConnectException: localhost:9092
```
**Solution:** 
```bash
# Démarrer Kafka dans Docker
docker-compose up -d kafka

# Vérifier les topics
docker exec <kafka-container> kafka-topics --list --bootstrap-server localhost:9092
```

### ❌ Messages pas consommés ?
**Vérifier:**
- `group-id` dans application.yml (doit être unique par service)
- `@KafkaListener(topics = "...")` sur la méthode
- Service consommateur bien lancé

### ❌ Événements perdus ?
**Kafka PERSISTE** les messages par défaut. Solution:
```yaml
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest  # Relire depuis le début si perdu
```

---

## 📈 Monitoring Kafka

### Vérifier les topics
```bash
docker exec kafka kafka-topics --list --bootstrap-server kafka:9092
```

### Vérifier les consommateurs
```bash
docker exec kafka kafka-consumer-groups --list --bootstrap-server kafka:9092
```

### Lire les messages d'un topic
```bash
docker exec kafka kafka-console-consumer --topic collecte.soumise \
  --from-beginning --bootstrap-server kafka:9092
```

---

## 🔐 Sécurité (Production)

En production, configurez:
```yaml
spring:
  kafka:
    security:
      protocol: SASL_SSL
    properties:
      sasl.mechanism: PLAIN
      sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username="user" password="pass";
```

---

## 📚 Résumé en une phrase

**Kafka = système nerveux central qui permet aux services de "parler" en temps réel sans se bloquer, tout en gardant un journal immuable de chaque événement.**

---

## 🚀 Prochaines étapes

1. ✅ Assurer que Kafka tourne (`docker-compose up -d`)
2. ✅ Ajouter des listeners pour les nouveaux événements (validation projet, etc.)
3. ✅ Créer un tableau de bord en temps réel alimenté par service-analytique
4. ✅ Monitorer les topics avec Kafka UI (optionnel mais utile)

