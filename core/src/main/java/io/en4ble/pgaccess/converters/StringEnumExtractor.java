package io.en4ble.pgaccess.converters;

import io.en4ble.pgaccess.enumerations.StringEnum;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

/**
 * @author Mark Hofmann (mark@en4ble.io)
 */
//@UnwrapByDefault
public class StringEnumExtractor implements ValueExtractor<@ExtractedValue(type = String.class) StringEnum> {

    @Override
    public void extractValues(StringEnum originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue == null ? null : originalValue.getKey());
    }
}
