package com.robby.speech.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "speeches")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Speech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String author;

    private String authorEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "speech_keywords", joinColumns = @JoinColumn(name = "speech_id"))
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();

    private LocalDate speechDate;

    public Speech(
        String text,
        String author,
        String authorEmail,
        Set<String> keywords,
        LocalDate speechDate
    ) {
        this.text = text;
        this.author = author;
        this.authorEmail = authorEmail;
        this.keywords = keywords;
        this.speechDate = speechDate;
    }
}
