-- Créer un cast implicite pour permettre à Hibernate d'insérer des Strings dans une colonne ENUM
CREATE CAST (varchar AS user_role) WITH INOUT AS IMPLICIT;
