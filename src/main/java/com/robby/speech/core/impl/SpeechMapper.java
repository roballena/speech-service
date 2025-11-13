package com.robby.speech.core.impl;

import com.robby.speech.controller.dto.SpeechDto;
import com.robby.speech.model.Speech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class SpeechMapper {

    private SpeechMapper() {
    }

    public static SpeechDto toDto(Speech speech) {
        if (speech == null) {
            return null;
        }

        return new SpeechDto(
            speech.getId(),
            speech.getText(),
            speech.getAuthor(),
            speech.getAuthorEmail(),
            new ArrayList<>(Optional.ofNullable(speech.getKeywords())
                .orElse(Collections.emptySet())),
            speech.getSpeechDate()
        );
    }

    public static Speech fromDto(SpeechDto dto) {
        if (dto == null) {
            return null;
        }

        Set<String> keywords = new HashSet<>(Optional.ofNullable(dto.keywords())
            .orElse(Collections.emptyList()));
        Speech speech = new Speech(
            dto.text(),
            dto.author(),
            dto.authorEmail(),
            keywords,
            dto.speechDate()
        );

        if (dto.id() != null) {
            speech.setId(dto.id());
        }
        return speech;
    }
}
