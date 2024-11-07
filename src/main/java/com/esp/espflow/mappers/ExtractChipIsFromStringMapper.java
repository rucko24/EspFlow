package com.esp.espflow.mappers;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rubn
 */
public class ExtractChipIsFromStringMapper {

    public static final ExtractChipIsFromStringMapper INSTANCE = new ExtractChipIsFromStringMapper();

    private ExtractChipIsFromStringMapper() {}

    /**
     * @param input
     * @return A {@link String}
     */
    public String getChipIsFromThisString(String input) {
        String chipRegex = "(?s)Chip is (\\S+).*?Features:";
        Pattern pattern = Pattern.compile(chipRegex);
        Matcher matcher = pattern.matcher(input);
        String chipIs = StringUtils.EMPTY;
        if (matcher.find()) {
            chipIs = matcher.group(1);
        }
        return Objects.equals(chipIs, StringUtils.EMPTY) ? "Can`t parsed this Chip" :
                chipIs.split("Features:")[0].concat(" executed flash_id successfully");
    }
}
