package com.robby.speech.core.impl;

import com.robby.speech.controller.dto.SpeechDto;
import com.robby.speech.model.Speech;
import com.robby.speech.repository.SpeechRepository;
import com.robby.speech.core.SpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SpeechServiceImpl implements SpeechService {

    private final SpeechRepository speechRepository;

    @Override
    public List<SpeechDto> findAll() {
        return speechRepository.findAll().stream()
            .map(SpeechMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public SpeechDto create(SpeechDto dto) {
        return SpeechMapper.toDto(speechRepository.save(
            SpeechMapper.fromDto(dto)
        ));
    }

    @Override
    public Optional<SpeechDto> update(Long id, SpeechDto dto) {
        return speechRepository.findById(id)
            .map(existing -> {
                if (dto.text() != null) {
                    existing.setText(dto.text());
                }
                if (dto.author() != null) {
                    existing.setAuthor(dto.author());
                }
                if (dto.authorEmail() != null) {
                    existing.setAuthorEmail(dto.authorEmail());
                }
                if (dto.keywords() != null) {
                    existing.setKeywords(new HashSet<>(dto.keywords()));
                }
                if (dto.speechDate() != null) {
                    existing.setSpeechDate(dto.speechDate());
                }
                return SpeechMapper.toDto(speechRepository.save(existing));
            });
    }

    @Override
    public boolean delete(Long id) {
        if (speechRepository.existsById(id)) {
            speechRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<SpeechDto> findById(Long id) {
        return speechRepository.findById(id).map(SpeechMapper::toDto);
    }

    @Override
    public List<SpeechDto> search(
        String author,
        LocalDate from,
        LocalDate to,
        String keyword,
        String text
    ) {
        List<Set<Long>> idSets = new ArrayList<>();

        if (author != null && !author.isBlank()) {
            idSets.add(speechRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        }
        if (from != null && to != null) {
            idSets.add(speechRepository.findBySpeechDateBetween(from, to)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        } else if (from != null) {
            idSets.add(speechRepository.findBySpeechDateGreaterThanEqual(from)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        } else if (to != null) {
            idSets.add(speechRepository.findBySpeechDateLessThanEqual(to)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        }
        if (keyword != null && !keyword.isBlank()) {
            idSets.add(speechRepository.findByKeywordLikeIgnoreCase(keyword)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        }
        if (text != null && !text.isBlank()) {
            idSets.add(speechRepository.findByTextContainingIgnoreCase(text)
                .stream()
                .map(Speech::getId)
                .collect(Collectors.toSet()));
        }

        List<Speech> results;
        if (idSets.isEmpty()) {
            results = speechRepository.findAll();
        } else {
            Set<Long> intersection = new HashSet<>(idSets.get(0));
            for (Set<Long> s : idSets.subList(1, idSets.size())) {
                intersection.retainAll(s);
            }
            if (intersection.isEmpty()) {
                results = Collections.emptyList();
            } else {
                results = speechRepository.findAllById(intersection);
            }
        }
        return results.stream().map(SpeechMapper::toDto).collect(Collectors.toList());
    }

}
