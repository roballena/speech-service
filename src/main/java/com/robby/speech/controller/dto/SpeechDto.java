package com.robby.speech.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record SpeechDto(
    Long id,
    String text,
    String author,
    String authorEmail,
    List<String> keywords,
    LocalDate speechDate
) {}

