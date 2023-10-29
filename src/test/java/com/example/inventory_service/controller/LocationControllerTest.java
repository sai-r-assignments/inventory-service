package com.example.inventory_service.controller;

import com.example.inventory_service.config.WebSecurityConfig;
import com.example.inventory_service.dto.LocationDto;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    @Captor
    private ArgumentCaptor<LocationDto> locationDtoArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_all_locations_all_roles() throws Exception {
        List<LocationDto> expectedResponse = List.of(LOCATION_DTO_1, LOCATION_DTO_2);
        when(locationService.getAllLocations())
                .thenReturn(expectedResponse);
        mockMvc.perform(get("/api/v1/location")
                        .with(getAdminUser()))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
        verify(locationService).getAllLocations();
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void test_get_all_locations_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/location").with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(locationService);
    }

    @Test
    void test_add_location_admin_role() throws Exception {
        mockMvc.perform(post("/api/v1/location")
                        .content(objectMapper.writeValueAsString(LOCATION_DTO_1))
                        .contentType(APPLICATION_JSON)
                        .with(getAdminUser()))
                .andExpect(status().isCreated());
        verify(locationService).addNewLocation(locationDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(locationService);
        LocationDto dto = locationDtoArgumentCaptor.getValue();
        verifyDto(dto, LOCATION_DTO_1);
    }

    @Test
    void test_add_location_common_role() throws Exception {
        mockMvc.perform(post("/api/v1/location")
                        .content(objectMapper.writeValueAsString(LOCATION_DTO_1))
                        .contentType(APPLICATION_JSON)
                        .with(getCommonUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(locationService);
    }

    @Test
    void test_add_location_location_already_exists() throws Exception {
        doThrow(new DataAlreadyExistsException("DAE"))
                .when(locationService).addNewLocation(any(LocationDto.class));
        mockMvc.perform(post("/api/v1/location")
                        .content(objectMapper.writeValueAsString(LOCATION_DTO_1))
                        .contentType(APPLICATION_JSON)
                        .with(getAdminUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("DAE"));
        verify(locationService).addNewLocation(locationDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(locationService);
        LocationDto dto = locationDtoArgumentCaptor.getValue();
        verifyDto(dto, LOCATION_DTO_1);
    }

    @Test
    void test_update_location_admin_role() throws Exception {
        mockMvc.perform(put("/api/v1/location")
                        .content(objectMapper.writeValueAsString(LOCATION_DTO_1))
                        .contentType(APPLICATION_JSON)
                        .with(getAdminUser()))
                .andExpect(status().isNoContent());
        verify(locationService).updateLocation(locationDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(locationService);
        LocationDto dto = locationDtoArgumentCaptor.getValue();
        verifyDto(dto, LOCATION_DTO_1);
    }

    @Test
    void test_update_location_common_role() throws Exception {
        mockMvc.perform(put("/api/v1/location")
                        .content(objectMapper.writeValueAsString(LOCATION_DTO_1))
                        .contentType(APPLICATION_JSON)
                        .with(getCommonUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(locationService);
    }

    @Test
    void test_delete_location_admin_role() throws Exception {
        mockMvc.perform(delete("/api/v1/location")
                        .param("locationId", LOCATION_ID_1)
                        .with(getAdminUser()))
                .andExpect(status().isNoContent());
        verify(locationService).deleteLocation(LOCATION_ID_1);
    }

    @Test
    void test_delete_location_common_role() throws Exception {
        mockMvc.perform(delete("/api/v1/location")
                        .param("locationId", LOCATION_ID_1)
                        .with(getCommonUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(locationService);
    }

    @Test
    void test_delete_location_constraint_violation() throws Exception {
        doThrow(new InvalidOperationException("IOE"))
                .when(locationService).deleteLocation(LOCATION_ID_1);
        mockMvc.perform(delete("/api/v1/location")
                        .param("locationId", LOCATION_ID_1)
                        .with(getAdminUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("IOE"));
        verify(locationService).deleteLocation(LOCATION_ID_1);
    }

    static Stream<Arguments> getAdminAndCommonUserArgs() {
        return Stream.of(
                arguments(getAdminUser()),
                arguments(getCommonUser())
        );
    }

    private void verifyDto(LocationDto dto, LocationDto expected) {
        Assertions.assertEquals(expected.locationId(), dto.locationId());
        Assertions.assertEquals(expected.description(), dto.description());
    }

}