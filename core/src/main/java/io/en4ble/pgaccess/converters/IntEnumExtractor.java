package io.en4ble.pgaccess.converters;

import io.en4ble.pgaccess.enumerations.IntEnum;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

/**
 * @author Mark Hofmann (mark@en4ble.io)
 */
//@UnwrapByDefault
public class IntEnumExtractor implements ValueExtractor<@ExtractedValue(type = Integer.class) IntEnum> {
    @Override
    public void extractValues(@ExtractedValue(type = Integer.class) IntEnum originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue == null ? null : originalValue.getKey());
    }
}
