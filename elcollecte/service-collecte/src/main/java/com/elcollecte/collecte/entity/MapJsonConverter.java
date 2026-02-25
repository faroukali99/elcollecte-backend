package com.elcollecte.collecte.entity;

/**
 * Placeholder inert MapJsonConverter kept for history; conversion is handled
 * now by Hibernate's JSON handling via @JdbcTypeCode(SqlTypes.JSON) on fields.
 * Keeping this class empty avoids any accidental AttributeConverter usage.
 */
@Deprecated
public final class MapJsonConverter {
    private MapJsonConverter() {}
}
