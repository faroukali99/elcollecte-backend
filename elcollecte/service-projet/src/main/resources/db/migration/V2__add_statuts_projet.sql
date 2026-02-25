-- Ajouter les nouveaux statuts à l'énumération projet_statut
ALTER TYPE projet_statut ADD VALUE 'VALIDE';
ALTER TYPE projet_statut ADD VALUE 'REJETE';

