package com.robby.speech.core.impl;

import com.robby.speech.controller.dto.SpeechDto;
import com.robby.speech.model.Speech;
import com.robby.speech.repository.SpeechRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeechServiceImplTest {

    @Mock
    private SpeechRepository speechRepository;

    @InjectMocks
    private SpeechServiceImpl speechService;

    private Speech speech1;
    private Speech speech2;
    private Speech speech3;
    private SpeechDto speechDto1;

    @BeforeEach
    void setUp() {
        speech1 = new Speech();
        speech1.setId(1L);
        speech1.setText("This is a test speech about technology");
        speech1.setAuthor("John Doe");
        speech1.setAuthorEmail("john@example.com");
        speech1.setKeywords(new HashSet<>(Arrays.asList("tech", "innovation")));
        speech1.setSpeechDate(LocalDate.of(2024, 1, 15));

        speech2 = new Speech();
        speech2.setId(2L);
        speech2.setText("Another speech about climate change");
        speech2.setAuthor("Jane Smith");
        speech2.setAuthorEmail("jane@example.com");
        speech2.setKeywords(new HashSet<>(Arrays.asList("climate", "environment")));
        speech2.setSpeechDate(LocalDate.of(2024, 2, 20));

        speech3 = new Speech();
        speech3.setId(3L);
        speech3.setText("Speech about technology and innovation");
        speech3.setAuthor("John Miller");
        speech3.setAuthorEmail("miller@example.com");
        speech3.setKeywords(new HashSet<>(Arrays.asList("tech", "future")));
        speech3.setSpeechDate(LocalDate.of(2024, 3, 10));

        speechDto1 = SpeechMapper.toDto(speech1);
    }

    // ============ findAll() Tests ============

    @Test
    void findAll_shouldReturnEmptyFind_whenNoDatabaseRecords() {
        when(speechRepository.findAll()).thenReturn(Collections.emptyList());

        List<SpeechDto> result = speechService.findAll();

        assertTrue(result.isEmpty());
        verify(speechRepository).findAll();
    }

    @Test
    void findAll_shouldReturnSingleSpeech_whenOneSpeechExists() {
        when(speechRepository.findAll()).thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.findAll();

        assertEquals(1, result.size());
        assertEquals(speech1.getId(), result.get(0).id());
        assertEquals(speech1.getText(), result.get(0).text());
        verify(speechRepository).findAll();
    }

    @Test
    void findAll_shouldReturnMultipleSpeeches_whenMultipleExist() {
        when(speechRepository.findAll()).thenReturn(Arrays.asList(speech1, speech2, speech3));

        List<SpeechDto> result = speechService.findAll();

        assertEquals(3, result.size());
        verify(speechRepository).findAll();
    }

    @Test
    void findAll_shouldCorrectlyMapAllFields() {
        when(speechRepository.findAll()).thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.findAll();

        SpeechDto dto = result.get(0);
        assertEquals(speech1.getId(), dto.id());
        assertEquals(speech1.getText(), dto.text());
        assertEquals(speech1.getAuthor(), dto.author());
        assertEquals(speech1.getAuthorEmail(), dto.authorEmail());
        assertEquals(speech1.getKeywords(), new HashSet<>(dto.keywords()));
        assertEquals(speech1.getSpeechDate(), dto.speechDate());
    }

    // ============ create() Tests ============

    @Test
    void create_shouldSaveAndReturnSpeech_withAllFields() {
        Speech savedSpeech = new Speech();
        savedSpeech.setId(1L);
        savedSpeech.setText(speechDto1.text());
        savedSpeech.setAuthor(speechDto1.author());
        savedSpeech.setAuthorEmail(speechDto1.authorEmail());
        savedSpeech.setKeywords(new HashSet<>(speechDto1.keywords()));
        savedSpeech.setSpeechDate(speechDto1.speechDate());

        when(speechRepository.save(any(Speech.class))).thenReturn(savedSpeech);

        SpeechDto result = speechService.create(speechDto1);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(speechDto1.text(), result.text());
        assertEquals(speechDto1.author(), result.author());
        verify(speechRepository).save(any(Speech.class));
    }

    @Test
    void create_shouldHandleMinimalData() {
        SpeechDto minimalDto = new SpeechDto(null, "Text only", null, null, null, null);
        Speech savedSpeech = new Speech();
        savedSpeech.setId(1L);
        savedSpeech.setText("Text only");

        when(speechRepository.save(any(Speech.class))).thenReturn(savedSpeech);

        SpeechDto result = speechService.create(minimalDto);

        assertNotNull(result);
        assertEquals("Text only", result.text());
        verify(speechRepository).save(any(Speech.class));
    }

    @Test
    void create_shouldSaveKeywordsCorrectly() {
        final List<String> keywordsList = Arrays.asList("keyword1", "keyword2", "keyword3");
        Set<String> keywordsSet = new HashSet<>(keywordsList);
        SpeechDto dtoWithKeywords = new SpeechDto(
            null, "Text", "Author", "email@test.com", keywordsList, LocalDate.now()
        );
        Speech savedSpeech = new Speech();
        savedSpeech.setId(1L);
        savedSpeech.setKeywords(keywordsSet);

        when(speechRepository.save(any(Speech.class))).thenReturn(savedSpeech);

        SpeechDto result = speechService.create(dtoWithKeywords);

        assertNotNull(result.keywords());
        assertEquals(3, result.keywords().size());
        verify(speechRepository).save(any(Speech.class));
    }

    @Test
    void create_shouldGenerateIdAfterSave() {
        SpeechDto dtoWithoutId = new SpeechDto(
            null,
            "Text",
            "Author",
            "email@test.com",
            null,
            null
        );
        Speech savedSpeech = new Speech();
        savedSpeech.setId(99L);
        savedSpeech.setText("Text");

        when(speechRepository.save(any(Speech.class))).thenReturn(savedSpeech);

        SpeechDto result = speechService.create(dtoWithoutId);

        assertNotNull(result.id());
        assertEquals(99L, result.id());
        verify(speechRepository).save(any(Speech.class));
    }

    // ============ update() Tests ============

    @Test
    void update_shouldUpdateAllFields_whenAllProvided() {
        SpeechDto updateDto = new SpeechDto(
            null,
            "Updated text",
            "Updated Author",
            "updated@email.com",
            Arrays.asList("new", "keywords"),
            LocalDate.of(2024, 12, 31)
        );

        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech1);

        Optional<SpeechDto> result = speechService.update(1L, updateDto);

        assertTrue(result.isPresent());
        verify(speechRepository).findById(1L);
        verify(speechRepository).save(speech1);
    }

    @Test
    void update_shouldUpdateOnlyText_whenOnlyTextProvided() {
        String originalAuthor = speech1.getAuthor();
        SpeechDto updateDto = new SpeechDto(null, "New text only", null, null, null, null);

        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech1);

        Optional<SpeechDto> result = speechService.update(1L, updateDto);

        assertTrue(result.isPresent());
        assertEquals("New text only", speech1.getText());
        assertEquals(originalAuthor, speech1.getAuthor());
        verify(speechRepository).save(speech1);
    }

    @Test
    void update_shouldUpdateOnlyAuthor_whenOnlyAuthorProvided() {
        String originalText = speech1.getText();
        SpeechDto updateDto = new SpeechDto(null, null, "New Author", null, null, null);

        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech1);

        Optional<SpeechDto> result = speechService.update(1L, updateDto);

        assertTrue(result.isPresent());
        assertEquals("New Author", speech1.getAuthor());
        assertEquals(originalText, speech1.getText());
        verify(speechRepository).save(speech1);
    }

    @Test
    void update_shouldReplaceKeywords_whenKeywordsProvided() {
        final List<String> newKeywordsList = Arrays.asList("completely", "new", "keywords");
        Set<String> newKeywordsSet = new HashSet<>(newKeywordsList);
        SpeechDto updateDto = new SpeechDto(null, null, null, null, newKeywordsList, null);

        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech1);

        Optional<SpeechDto> result = speechService.update(1L, updateDto);

        assertTrue(result.isPresent());
        assertEquals(newKeywordsSet, speech1.getKeywords());
        verify(speechRepository).save(speech1);
    }

    @Test
    void update_shouldNotOverwriteWithNull_whenFieldsAreNull() {
        String originalText = speech1.getText();
        String originalAuthor = speech1.getAuthor();
        SpeechDto updateDto = new SpeechDto(null, null, null, null, null, null);

        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech1);

        Optional<SpeechDto> result = speechService.update(1L, updateDto);

        assertTrue(result.isPresent());
        assertEquals(originalText, speech1.getText());
        assertEquals(originalAuthor, speech1.getAuthor());
        verify(speechRepository).save(speech1);
    }

    @Test
    void update_shouldReturnEmpty_whenSpeechNotFound() {
        SpeechDto updateDto = new SpeechDto(null, "Updated text", null, null, null, null);

        when(speechRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<SpeechDto> result = speechService.update(999L, updateDto);

        assertFalse(result.isPresent());
        verify(speechRepository).findById(999L);
        verify(speechRepository, never()).save(any());
    }

    // ============ delete() Tests ============

    @Test
    void delete_shouldReturnTrue_whenSpeechExists() {
        when(speechRepository.existsById(1L)).thenReturn(true);
        doNothing().when(speechRepository).deleteById(1L);

        boolean result = speechService.delete(1L);

        assertTrue(result);
        verify(speechRepository).existsById(1L);
        verify(speechRepository).deleteById(1L);
    }

    @Test
    void delete_shouldReturnFalse_whenSpeechDoesNotExist() {
        when(speechRepository.existsById(999L)).thenReturn(false);

        boolean result = speechService.delete(999L);

        assertFalse(result);
        verify(speechRepository).existsById(999L);
        verify(speechRepository, never()).deleteById(anyLong());
    }

    // ============ findById() Tests ============

    @Test
    void findById_shouldReturnSpeech_whenExists() {
        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));

        Optional<SpeechDto> result = speechService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(speech1.getId(), result.get().id());
        assertEquals(speech1.getText(), result.get().text());
        verify(speechRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(speechRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<SpeechDto> result = speechService.findById(999L);

        assertFalse(result.isPresent());
        verify(speechRepository).findById(999L);
    }

    @Test
    void findById_shouldMapAllFieldsCorrectly() {
        when(speechRepository.findById(1L)).thenReturn(Optional.of(speech1));

        Optional<SpeechDto> result = speechService.findById(1L);

        assertTrue(result.isPresent());
        SpeechDto dto = result.get();
        assertEquals(speech1.getId(), dto.id());
        assertEquals(speech1.getText(), dto.text());
        assertEquals(speech1.getAuthor(), dto.author());
        assertEquals(speech1.getAuthorEmail(), dto.authorEmail());
        assertEquals(speech1.getKeywords(), new HashSet<>(dto.keywords()));
        assertEquals(speech1.getSpeechDate(), dto.speechDate());
    }

    // ============ search() Tests ============

    @Test
    void search_shouldFindByAuthor_caseInsensitive() {
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findAllById(any())).thenReturn(Arrays.asList(speech1, speech3));

        List<SpeechDto> result = speechService.search("john", null, null, null, null);

        assertEquals(2, result.size());
        verify(speechRepository).findByAuthorContainingIgnoreCase("john");
    }

    @Test
    void search_shouldFindByKeyword() {
        when(speechRepository.findByKeywordLikeIgnoreCase("tech"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findAllById(any())).thenReturn(Arrays.asList(speech1, speech3));

        List<SpeechDto> result = speechService.search(null, null, null, "tech", null);

        assertEquals(2, result.size());
        verify(speechRepository).findByKeywordLikeIgnoreCase("tech");
    }

    @Test
    void search_shouldFindByText_caseInsensitive() {
        when(speechRepository.findByTextContainingIgnoreCase("technology"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findAllById(any())).thenReturn(Arrays.asList(speech1, speech3));

        List<SpeechDto> result = speechService.search(null, null, null, null, "technology");

        assertEquals(2, result.size());
        verify(speechRepository).findByTextContainingIgnoreCase("technology");
    }

    @Test
    void search_shouldFindByFromDate() {
        LocalDate fromDate = LocalDate.of(2024, 2, 1);
        when(speechRepository.findBySpeechDateGreaterThanEqual(fromDate))
            .thenReturn(Arrays.asList(speech2, speech3));
        when(speechRepository.findAllById(any())).thenReturn(Arrays.asList(speech2, speech3));

        List<SpeechDto> result = speechService.search(null, fromDate, null, null, null);

        assertEquals(2, result.size());
        verify(speechRepository).findBySpeechDateGreaterThanEqual(fromDate);
    }

    @Test
    void search_shouldFindByToDate() {
        LocalDate toDate = LocalDate.of(2024, 2, 1);
        when(speechRepository.findBySpeechDateLessThanEqual(toDate))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(any())).thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search(null, null, toDate, null, null);

        assertEquals(1, result.size());
        verify(speechRepository).findBySpeechDateLessThanEqual(toDate);
    }

    @Test
    void search_shouldFindByDateRange() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 2, 28);
        when(speechRepository.findBySpeechDateBetween(from, to))
            .thenReturn(Arrays.asList(speech1, speech2));
        when(speechRepository.findAllById(any())).thenReturn(Arrays.asList(speech1, speech2));

        List<SpeechDto> result = speechService.search(null, from, to, null, null);

        assertEquals(2, result.size());
        verify(speechRepository).findBySpeechDateBetween(from, to);
    }

    @Test
    void search_shouldFindByAuthorAndKeyword_intersection() {
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findByKeywordLikeIgnoreCase("innovation"))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(Collections.singleton(1L)))
            .thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search("john", null, null, "innovation", null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void search_shouldFindByAuthorAndDateRange() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 2, 1);
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findBySpeechDateBetween(from, to))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(Collections.singleton(1L)))
            .thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search("john", from, to, null, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void search_shouldFindByTextAndKeyword() {
        when(speechRepository.findByTextContainingIgnoreCase("technology"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findByKeywordLikeIgnoreCase("innovation"))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(Collections.singleton(1L)))
            .thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search(null, null, null, "innovation", "technology");

        assertEquals(1, result.size());
    }

    @Test
    void search_shouldFindByAllParameters() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findBySpeechDateBetween(from, to))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findByKeywordLikeIgnoreCase("tech"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findByTextContainingIgnoreCase("test"))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(Collections.singleton(1L)))
            .thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search("john", from, to, "tech", "test");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void search_shouldReturnEmpty_whenNoIntersection() {
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Arrays.asList(speech1, speech3));
        when(speechRepository.findByKeywordLikeIgnoreCase("climate"))
            .thenReturn(Collections.singletonList(speech2));

        List<SpeechDto> result = speechService.search("john", null, null, "climate", null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldReturnAll_whenNoParameters() {
        when(speechRepository.findAll()).thenReturn(Arrays.asList(speech1, speech2, speech3));

        List<SpeechDto> result = speechService.search(null, null, null, null, null);

        assertEquals(3, result.size());
        verify(speechRepository).findAll();
    }

    @Test
    void search_shouldIgnoreBlankStrings() {
        when(speechRepository.findAll()).thenReturn(Arrays.asList(speech1, speech2, speech3));

        List<SpeechDto> result = speechService.search("", null, null, "  ", "");

        assertEquals(3, result.size());
        verify(speechRepository).findAll();
        verify(speechRepository, never()).findByAuthorContainingIgnoreCase(any());
        verify(speechRepository, never()).findByKeywordLikeIgnoreCase(any());
        verify(speechRepository, never()).findByTextContainingIgnoreCase(any());
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() {
        when(speechRepository.findByAuthorContainingIgnoreCase("nonexistent"))
            .thenReturn(Collections.emptyList());

        List<SpeechDto> result = speechService.search("nonexistent", null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldReturnSingleResult() {
        when(speechRepository.findByAuthorContainingIgnoreCase("jane"))
            .thenReturn(Collections.singletonList(speech2));
        when(speechRepository.findAllById(Collections.singleton(2L)))
            .thenReturn(Collections.singletonList(speech2));

        List<SpeechDto> result = speechService.search("jane", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).id());
    }

    @Test
    void search_shouldMapResultsCorrectly() {
        when(speechRepository.findByAuthorContainingIgnoreCase("john"))
            .thenReturn(Collections.singletonList(speech1));
        when(speechRepository.findAllById(Collections.singleton(1L)))
            .thenReturn(Collections.singletonList(speech1));

        List<SpeechDto> result = speechService.search("john", null, null, null, null);

        assertEquals(1, result.size());
        SpeechDto dto = result.get(0);
        assertEquals(speech1.getId(), dto.id());
        assertEquals(speech1.getText(), dto.text());
        assertEquals(speech1.getAuthor(), dto.author());
        assertEquals(speech1.getAuthorEmail(), dto.authorEmail());
    }
}