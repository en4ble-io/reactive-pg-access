package io.en4ble.pgaccess.converters;

import io.en4ble.pgaccess.enumerations.TypedEnum;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

/**
 * @author Mark Hofmann (mark@en4ble.io)
 */
@UnwrapByDefault
public class TypedEnumExtractor implements ValueExtractor<@ExtractedValue(type = String.class) TypedEnum<?>> {
    @Override
    public void extractValues(@ExtractedValue(type = String.class) TypedEnum<?> originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue == null ? null : originalValue.toString());
    }
}
