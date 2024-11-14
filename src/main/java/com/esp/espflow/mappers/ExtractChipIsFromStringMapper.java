package com.esp.espflow.mappers;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rubn
 */
public final class ExtractChipIsFromStringMapper {

    public static final ExtractChipIsFromStringMapper INSTANCE = new ExtractChipIsFromStringMapper();

    private ExtractChipIsFromStringMapper() {}

    /**
     * @param input
     * @return A {@link String}
     */
    public String getChipIsFromThisString(String input) {
        final String notNullInput = Objects.isNull(input) ? StringUtils.EMPTY : input;
        final String chipRegex = "(?s)Chip is (\\S+).*?Features:";
        final Pattern pattern = Pattern.compile(chipRegex);
        final Matcher matcher = pattern.matcher(notNullInput);
        String chipIs = StringUtils.EMPTY;
        if (matcher.find()) {
            chipIs = matcher.group(1);
        }
        return Objects.equals(chipIs, StringUtils.EMPTY)
                ? "This chip cannot be parsed"
                : chipIs.split("Features:")[0];
    }
}
