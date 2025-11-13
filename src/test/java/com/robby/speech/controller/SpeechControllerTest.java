package com.robby.speech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robby.speech.controller.dto.SpeechDto;
import com.robby.speech.core.SpeechService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpeechController.class)
class SpeechControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpeechService speechService;

    private SpeechDto speechDto1;
    private SpeechDto speechDto2;
    private SpeechDto speechDto3;

    @BeforeEach
    void setUp() {
        speechDto1 = new SpeechDto(
            1L,
            "This is a test speech about technology",
            "John Doe",
            "john@example.com",
            Arrays.asList("tech", "innovation"),
            LocalDate.of(2024, 1, 15)
        );

        speechDto2 = new SpeechDto(
            2L,
            "Another speech about climate change",
            "Jane Smith",
            "jane@example.com",
            Arrays.asList("climate", "environment"),
            LocalDate.of(2024, 2, 20)
        );

        speechDto3 = new SpeechDto(
            3L,
            "Speech about technology and innovation",
            "John Miller",
            "miller@example.com",
            Arrays.asList("tech", "future"),
            LocalDate.of(2024, 3, 10)
        );
    }

    // ============ GET /api/speeches - findAll() Tests ============

    @Test
    void findAll_shouldReturnEmptyFind_whenNoSpeeches() throws Exception {
        when(speechService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/speeches"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));

        verify(speechService).findAll();
    }

    @Test
    void findAll_shouldReturnSingleSpeech() throws Exception {
        when(speechService.findAll()).thenReturn(Collections.singletonList(speechDto1));

        mockMvc.perform(get("/api/speeches"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].text", is("This is a test speech about technology")))
            .andExpect(jsonPath("$[0].author", is("John Doe")))
            .andExpect(jsonPath("$[0].authorEmail", is("john@example.com")));

        verify(speechService).findAll();
    }

    @Test
    void findAll_shouldReturnMultipleSpeeches() throws Exception {
        when(speechService.findAll()).thenReturn(Arrays.asList(speechDto1, speechDto2, speechDto3));

        mockMvc.perform(get("/api/speeches"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[2].id", is(3)));

        verify(speechService).findAll();
    }

    @Test
    void findAll_shouldReturnKeywordsAsArray() throws Exception {
        when(speechService.findAll()).thenReturn(Collections.singletonList(speechDto1));

        mockMvc.perform(get("/api/speeches"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].keywords", hasSize(2)))
            .andExpect(jsonPath("$[0].keywords", containsInAnyOrder("tech", "innovation")));

        verify(speechService).findAll();
    }

    @Test
    void findAll_shouldReturnSpeechDateInCorrectFormat() throws Exception {
        when(speechService.findAll()).thenReturn(Collections.singletonList(speechDto1));

        mockMvc.perform(get("/api/speeches"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].speechDate", is("2024-01-15")));

        verify(speechService).findAll();
    }

    // ============ GET /api/speeches/{id} - getById() Tests ============

    @Test
    void getById_shouldReturnSpeech_whenExists() throws Exception {
        when(speechService.findById(1L)).thenReturn(Optional.of(speechDto1));

        mockMvc.perform(get("/api/speeches/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.text", is("This is a test speech about technology")))
            .andExpect(jsonPath("$.author", is("John Doe")))
            .andExpect(jsonPath("$.authorEmail", is("john@example.com")));

        verify(speechService).findById(1L);
    }

    @Test
    void getById_shouldReturn404_whenNotExists() throws Exception {
        when(speechService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/speeches/999"))
            .andExpect(status().isNotFound());

        verify(speechService).findById(999L);
    }

    @Test
    void getById_shouldReturnAllFields_includingKeywordsAndDate() throws Exception {
        when(speechService.findById(1L)).thenReturn(Optional.of(speechDto1));

        mockMvc.perform(get("/api/speeches/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keywords", hasSize(2)))
            .andExpect(jsonPath("$.keywords", containsInAnyOrder("tech", "innovation")))
            .andExpect(jsonPath("$.speechDate", is("2024-01-15")));

        verify(speechService).findById(1L);
    }

    @Test
    void getById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/speeches/invalid"))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).findById(anyLong());
    }

    // ============ POST /api/speeches - create() Tests ============

    @Test
    void create_shouldReturnCreatedSpeech_withAllFields() throws Exception {
        SpeechDto inputDto = new SpeechDto(
            null,
            "New speech text",
            "New Author",
            "author@example.com",
            Arrays.asList("keyword1", "keyword2"),
            LocalDate.of(2024, 5, 1)
        );

        SpeechDto createdDto = new SpeechDto(
            1L,
            "New speech text",
            "New Author",
            "author@example.com",
            Arrays.asList("keyword1", "keyword2"),
            LocalDate.of(2024, 5, 1)
        );

        when(speechService.create(any(SpeechDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/speeches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.text", is("New speech text")))
            .andExpect(jsonPath("$.author", is("New Author")))
            .andExpect(jsonPath("$.authorEmail", is("author@example.com")));

        verify(speechService).create(any(SpeechDto.class));
    }

    @Test
    void create_shouldHandleMinimalData() throws Exception {
        SpeechDto minimalDto = new SpeechDto(null, "Just text", null, null, null, null);
        SpeechDto createdDto = new SpeechDto(1L, "Just text", null, null, null, null);

        when(speechService.create(any(SpeechDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/speeches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.text", is("Just text")));

        verify(speechService).create(any(SpeechDto.class));
    }

    @Test
    void create_shouldHandleKeywords() throws Exception {
        SpeechDto inputDto = new SpeechDto(
            null,
            "Text",
            "Author",
            "email@test.com",
            Arrays.asList("key1", "key2", "key3"),
            null
        );

        SpeechDto createdDto = new SpeechDto(
            1L,
            "Text",
            "Author",
            "email@test.com",
            Arrays.asList("key1", "key2", "key3"),
            null
        );

        when(speechService.create(any(SpeechDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/speeches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keywords", hasSize(3)))
            .andExpect(jsonPath("$.keywords", containsInAnyOrder("key1", "key2", "key3")));

        verify(speechService).create(any(SpeechDto.class));
    }

    @Test
    void create_shouldReturn400_whenInvalidJson() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/api/speeches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).create(any());
    }

    @Test
    void create_shouldReturn400_whenMissingContentType() throws Exception {
        mockMvc.perform(post("/api/speeches")
                .content(objectMapper.writeValueAsString(speechDto1)))
            .andExpect(status().isUnsupportedMediaType());

        verify(speechService, never()).create(any());
    }

    @Test
    void create_shouldHandleDateInCorrectFormat() throws Exception {
        SpeechDto inputDto = new SpeechDto(
            null,
            "Text",
            "Author",
            "email@test.com",
            null,
            LocalDate.of(2024, 12, 25)
        );

        SpeechDto createdDto = new SpeechDto(
            1L,
            "Text",
            "Author",
            "email@test.com",
            null,
            LocalDate.of(2024, 12, 25)
        );

        when(speechService.create(any(SpeechDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/speeches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.speechDate", is("2024-12-25")));

        verify(speechService).create(any(SpeechDto.class));
    }

    // ============ PUT /api/speeches/{id} - update() Tests ============

    @Test
    void update_shouldReturnUpdatedSpeech_whenExists() throws Exception {
        SpeechDto updateDto = new SpeechDto(
            null,
            "Updated text",
            "Updated Author",
            "updated@example.com",
            Arrays.asList("updated", "keywords"),
            LocalDate.of(2024, 6, 1)
        );

        SpeechDto updatedDto = new SpeechDto(
            1L,
            "Updated text",
            "Updated Author",
            "updated@example.com",
            Arrays.asList("updated", "keywords"),
            LocalDate.of(2024, 6, 1)
        );

        when(speechService.update(
            eq(1L),
            any(SpeechDto.class)
        )).thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/speeches/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.text", is("Updated text")))
            .andExpect(jsonPath("$.author", is("Updated Author")));

        verify(speechService).update(eq(1L), any(SpeechDto.class));
    }

    @Test
    void update_shouldReturn404_whenNotExists() throws Exception {
        SpeechDto updateDto = new SpeechDto(
            null,
            "Updated text",
            null,
            null,
            null,
            null
        );

        when(speechService.update(eq(999L), any(SpeechDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/speeches/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isNotFound());

        verify(speechService).update(eq(999L), any(SpeechDto.class));
    }

    @Test
    void update_shouldHandlePartialUpdate() throws Exception {
        SpeechDto partialDto = new SpeechDto(null, "Only text updated", null, null, null, null);
        SpeechDto updatedDto = new SpeechDto(
            1L,
            "Only text updated",
            "John Doe",
            "john@example.com",
            Arrays.asList("tech", "innovation"),
            LocalDate.of(2024, 1, 15)
        );

        when(speechService.update(
            eq(1L),
            any(SpeechDto.class)
        )).thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/speeches/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text", is("Only text updated")))
            .andExpect(jsonPath("$.author", is("John Doe")));

        verify(speechService).update(eq(1L), any(SpeechDto.class));
    }

    @Test
    void update_shouldReturn400_whenInvalidJson() throws Exception {
        String invalidJson = "{invalid}";

        mockMvc.perform(put("/api/speeches/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).update(anyLong(), any());
    }

    @Test
    void update_shouldHandleInvalidIdFormat() throws Exception {
        SpeechDto updateDto = new SpeechDto(null, "Text", null, null, null, null);

        mockMvc.perform(put("/api/speeches/invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).update(anyLong(), any());
    }

    // ============ DELETE /api/speeches/{id} - delete() Tests ============

    @Test
    void delete_shouldReturn204_whenDeleted() throws Exception {
        when(speechService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/speeches/1"))
            .andExpect(status().isNoContent());

        verify(speechService).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenNotExists() throws Exception {
        when(speechService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/speeches/999"))
            .andExpect(status().isNotFound());

        verify(speechService).delete(999L);
    }

    @Test
    void delete_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/speeches/invalid"))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).delete(anyLong());
    }

    // ============ GET /api/speeches/search - search() Tests ============

    @Test
    void search_shouldReturnResults_withAuthorParameter() throws Exception {
        when(speechService.search(eq("john"), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(Arrays.asList(speechDto1, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "john"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].author", containsString("John")))
            .andExpect(jsonPath("$[1].author", containsString("John")));

        verify(speechService).search(eq("john"), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void search_shouldReturnResults_withKeywordParameter() throws Exception {
        when(speechService.search(isNull(), isNull(), isNull(), eq("tech"), isNull()))
            .thenReturn(Arrays.asList(speechDto1, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("keyword", "tech"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(speechService).search(isNull(), isNull(), isNull(), eq("tech"), isNull());
    }

    @Test
    void search_shouldReturnResults_withTextParameter() throws Exception {
        when(speechService.search(isNull(), isNull(), isNull(), isNull(), eq("technology")))
            .thenReturn(Arrays.asList(speechDto1, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("text", "technology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(speechService).search(isNull(), isNull(), isNull(), isNull(), eq("technology"));
    }

    @Test
    void search_shouldReturnResults_withFromDateParameter() throws Exception {
        LocalDate fromDate = LocalDate.of(2024, 2, 1);
        when(speechService.search(isNull(), eq(fromDate), isNull(), isNull(), isNull()))
            .thenReturn(Arrays.asList(speechDto2, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("from", "2024-02-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(speechService).search(isNull(), eq(fromDate), isNull(), isNull(), isNull());
    }

    @Test
    void search_shouldReturnResults_withToDateParameter() throws Exception {
        LocalDate toDate = LocalDate.of(2024, 2, 1);
        when(speechService.search(isNull(), isNull(), eq(toDate), isNull(), isNull()))
            .thenReturn(Collections.singletonList(speechDto1));

        mockMvc.perform(get("/api/speeches/search")
                .param("to", "2024-02-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(speechService).search(isNull(), isNull(), eq(toDate), isNull(), isNull());
    }

    @Test
    void search_shouldReturnResults_withDateRange() throws Exception {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 2, 28);
        when(speechService.search(isNull(), eq(from), eq(to), isNull(), isNull()))
            .thenReturn(Arrays.asList(speechDto1, speechDto2));

        mockMvc.perform(get("/api/speeches/search")
                .param("from", "2024-01-01")
                .param("to", "2024-02-28"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(speechService).search(isNull(), eq(from), eq(to), isNull(), isNull());
    }

    @Test
    void search_shouldReturnResults_withMultipleParameters() throws Exception {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        when(speechService.search(eq("john"), eq(from), eq(to), eq("tech"), eq("technology")))
            .thenReturn(Arrays.asList(speechDto1, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "john")
                .param("from", "2024-01-01")
                .param("to", "2024-03-31")
                .param("keyword", "tech")
                .param("text", "technology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        verify(speechService).search(eq("john"), eq(from), eq(to), eq("tech"), eq("technology"));
    }

    @Test
    void search_shouldReturnAllSpeeches_whenNoParameters() throws Exception {
        when(speechService.search(isNull(), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(Arrays.asList(speechDto1, speechDto2, speechDto3));

        mockMvc.perform(get("/api/speeches/search"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));

        verify(speechService).search(isNull(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() throws Exception {
        when(speechService.search(eq("nonexistent"), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(speechService).search(eq("nonexistent"), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void search_shouldReturn400_whenInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/speeches/search")
                .param("from", "invalid-date"))
            .andExpect(status().isBadRequest());

        verify(speechService, never()).search(any(), any(), any(), any(), any());
    }

    @Test
    void search_shouldHandleEmptyStringParameters() throws Exception {
        when(speechService.search(eq(""), isNull(), isNull(), eq(""), eq("")))
            .thenReturn(Arrays.asList(speechDto1, speechDto2, speechDto3));

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "")
                .param("keyword", "")
                .param("text", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));

        verify(speechService).search(eq(""), isNull(), isNull(), eq(""), eq(""));
    }

    @Test
    void search_shouldHandleSpecialCharactersInParameters() throws Exception {
        when(speechService.search(eq("O'Brien"), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "O'Brien"))
            .andExpect(status().isOk());

        verify(speechService).search(eq("O'Brien"), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void search_shouldReturnResultsWithAllFields() throws Exception {
        when(speechService.search(eq("john"), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(Collections.singletonList(speechDto1));

        mockMvc.perform(get("/api/speeches/search")
                .param("author", "john"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].text", is("This is a test speech about technology")))
            .andExpect(jsonPath("$[0].author", is("John Doe")))
            .andExpect(jsonPath("$[0].authorEmail", is("john@example.com")))
            .andExpect(jsonPath("$[0].keywords", hasSize(2)))
            .andExpect(jsonPath("$[0].speechDate", is("2024-01-15")));

        verify(speechService).search(eq("john"), isNull(), isNull(), isNull(), isNull());
    }
}