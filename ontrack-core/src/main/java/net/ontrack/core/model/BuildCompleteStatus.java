package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildCompleteStatus {

    // Build general info
    private final int id;
    private final String name;
    private final String description;
    private final DatedSignature signature;
    // List of decorations
    private final List<LocalizedDecoration> decorations;
    // List of validation stamps with their associated runs for this build
    private final Map<String, BuildValidationStamp> validationStamps;
    // List of promotion levels for this build
    private final List<BuildPromotionLevel> promotionLevels;

    public BuildCompleteStatus(int id, String name, String description, DatedSignature signature, List<LocalizedDecoration> decorations, List<BuildValidationStamp> stamps, List<BuildPromotionLevel> promotionLevels) {
        this(
                id, name, description,
                signature,
                decorations,
                new TreeMap<>(
                        Maps.uniqueIndex(stamps, new Function<BuildValidationStamp, String>() {
                            @Override
                            public String apply(BuildValidationStamp stamp) {
                                return stamp.getName();
                            }
                        })
                ), promotionLevels);
    }

}
