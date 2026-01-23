package com.lokoko.global.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class RatingConverter {

	private static final int DEFAULT_SCALE = 1;
	private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

	private RatingConverter() {
	}

	public static double toDisplayRating(Double averageRating) {
		return toDisplayRating(averageRating, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
	}

	public static double toDisplayRating(Double averageRating, int scale, RoundingMode roundingMode) {
		if (averageRating == null) {
			return 0.0;
		}

		return BigDecimal.valueOf(averageRating)
			.setScale(scale, roundingMode)
			.doubleValue();
	}
}
