package com.example.DATN.utils.components;

import com.example.DATN.utils.enums.options.Action;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToActionConverter implements Converter<String, Action> {
    @Override
    public Action convert(String source) {
        return Action.valueOf(source.toUpperCase());
    }
}
