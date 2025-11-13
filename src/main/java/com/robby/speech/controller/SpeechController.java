package com.robby.speech.controller;

import com.robby.speech.controller.dto.SpeechDto;
import com.robby.speech.core.SpeechService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/speeches")
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @GetMapping
    public List<SpeechDto> findAll() {
        return speechService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeechDto> getById(@PathVariable Long id) {
        return speechService.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SpeechDto> create(@RequestBody SpeechDto dto) {
        SpeechDto created = speechService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpeechDto> update(@PathVariable Long id, @RequestBody SpeechDto dto) {
        return speechService.update(id, dto)
            .map(ResponseEntity::ok)
            .orElseGet(
                () -> ResponseEntity.notFound().build()
            );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = speechService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<SpeechDto> search(
        @RequestParam(required = false) String author,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String text
    ) {
        return speechService.search(author, from, to, keyword, text);
    }
}
