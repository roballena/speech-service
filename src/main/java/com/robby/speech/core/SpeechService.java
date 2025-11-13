package com.robby.speech.core;

import com.robby.speech.controller.dto.SpeechDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpeechService {

    List<SpeechDto> findAll();
    SpeechDto create(SpeechDto dto);
    Optional<SpeechDto> update(Long id, SpeechDto dto);
    boolean delete(Long id);
    Optional<SpeechDto> findById(Long id);
    List<SpeechDto> search(
        String author,
        LocalDate from,
        LocalDate to,
        String keyword,
        String text
    );
}
