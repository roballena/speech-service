package com.robby.speech.repository;

import com.robby.speech.model.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpeechRepository extends JpaRepository<Speech, Long> {

    List<Speech> findByAuthorContainingIgnoreCase(String author);
    List<Speech> findBySpeechDateBetween(LocalDate from, LocalDate to);
    List<Speech> findBySpeechDateGreaterThanEqual(LocalDate from);
    List<Speech> findBySpeechDateLessThanEqual(LocalDate to);
    List<Speech> findByTextContainingIgnoreCase(String text);

    @Query(
        "select s from Speech s join s.keywords k where lower(k) like lower(concat('%', :keyword," +
            " '%'))")
    List<Speech> findByKeywordLikeIgnoreCase(@Param("keyword") String keyword);
}
