package com.esp.espflow.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @author rubn
 */
@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return "";
        }
        // Une el array en una sola cadena separada por comas
        return String.join(SPLIT_CHAR, attribute);
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new String[0];
        }
        // Separa la cadena para recuperar el array
        return dbData.split(SPLIT_CHAR);
    }
}