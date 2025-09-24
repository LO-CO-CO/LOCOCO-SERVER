package com.lokoko.global.utils;

import com.lokoko.domain.productReview.domain.entity.enums.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Rating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Rating.fromValue(dbData);
    }
}
