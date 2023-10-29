package com.example.inventory_service.controller;

import com.example.inventory_service.config.WebSecurityConfig;
import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.service.MetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(WebSecurityConfig.class)
@WebMvcTest(MetadataController.class)
class MetadataControllerTest {

    private static final MetadataDto DTO = new MetadataDto(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MetadataService metadataService;

    @Captor
    private ArgumentCaptor<MetadataDto> metadataDtoArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_all_departments_with_given_location_for_admin_and_common_user(
            RequestPostProcessor role) throws Exception {
        Collection<String> expectedResponse = getTestStrings();
        when(metadataService.getAllDepartmentsWithLocation(LOCATION_ID_1))
                .thenReturn(expectedResponse);
        mockMvc.perform(get("/api/v1/location/{location_id}/department", LOCATION_ID_1)
                        .with(role))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
        verify(metadataService).getAllDepartmentsWithLocation(LOCATION_ID_1);
        verifyNoMoreInteractions(metadataService);
    }

    @Test
    void test_get_all_departments_with_given_location_for_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/location/{location_id}/department", LOCATION_ID_1)
                        .with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_all_categories_with_given_location_dept_for_admin_and_common_user(
            RequestPostProcessor role) throws Exception {
        Collection<String> expectedResponse = getTestStrings();
        when(metadataService.getAllCategoriesWithLocationAndDepartment(LOCATION_ID_1, DEPARTMENT_1))
                .thenReturn(expectedResponse);
        mockMvc.perform(get("/api/v1/location/{location_id}/department/{department_id}/category",
                        LOCATION_ID_1, DEPARTMENT_1).with(role))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
        verify(metadataService).getAllCategoriesWithLocationAndDepartment(LOCATION_ID_1, DEPARTMENT_1);
        verifyNoMoreInteractions(metadataService);
    }

    @Test
    void test_get_all_categories_with_given_location_dept_for_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/location/{location_id}/" +
                                "department/{department_id}/category", LOCATION_ID_1, DEPARTMENT_1)
                        .with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_all_sub_categories_with_given_location_dept_category_for_admin_and_common_user(
            RequestPostProcessor role) throws Exception {
        Collection<String> expectedResponse = getTestStrings();
        when(metadataService.getAllSubCategoriesWithLocationAndDepartmentAndCategory(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1))
                .thenReturn(expectedResponse);
        mockMvc.perform(get("/api/v1/location/{location_id}/" +
                                "department/{department_id}/category/{category_id}/subcategory",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1)
                        .with(role))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
        verify(metadataService).getAllSubCategoriesWithLocationAndDepartmentAndCategory(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1);
        verifyNoMoreInteractions(metadataService);
    }

    @Test
    void test_get_all_sub_categories_with_given_location_dept_category_for_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/location/{location_id}/" +
                                "department/{department_id}/category/{category_id}/subcategory",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1)
                        .with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_metadata_with_all_details_for_admin_and_common_user(
            RequestPostProcessor role) throws Exception {
        when(metadataService.getMetadataFromInventoryDetails(any(MetadataDto.class)))
                .thenReturn(METADATA_1);
        mockMvc.perform(get("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(role))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(DTO)));
        verify(metadataService).getMetadataFromInventoryDetails(metadataDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataService);
        verifyMetadataDTo(metadataDtoArgumentCaptor.getValue());
    }

    @Test
    void test_get_metadata_with_all_details_for_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    @Test
    void test_add_metadata_with_all_details_for_admin_user() throws Exception {
        mockMvc.perform(post("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(getAdminUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(metadataService).addMetadata(metadataDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataService);
        verifyMetadataDTo(metadataDtoArgumentCaptor.getValue());
    }

    @ParameterizedTest
    @MethodSource("getCommonUserAndAnonymousUserArgs")
    void test_add_metadata_with_all_details_for_common_and_anonymous_users(
            RequestPostProcessor role) throws Exception {
        mockMvc.perform(post("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    @Test
    void test_delete_metadata_with_all_details_for_admin_user() throws Exception {
        mockMvc.perform(delete("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(getAdminUser())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(metadataService).deleteMetadata(metadataDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataService);
        verifyMetadataDTo(metadataDtoArgumentCaptor.getValue());
    }

    @ParameterizedTest
    @MethodSource("getCommonUserAndAnonymousUserArgs")
    void test_delete_metadata_with_all_details_for_common_and_anonymous_users(
            RequestPostProcessor role) throws Exception {
        mockMvc.perform(delete("/api/v1/location/{location_id}/department/" +
                                "{department_id}/category/{category_id}/subcategory/{subcategory_id}",
                        LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1)
                        .with(role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verifyNoInteractions(metadataService);
    }

    private Collection<String> getTestStrings() {
        return List.of("Str1", "Str2", "Str3");
    }

    static Stream<Arguments> getAdminAndCommonUserArgs() {
        return java.util.stream.Stream.of(
                arguments(getAdminUser()),
                arguments(getCommonUser())
        );
    }

    static Stream<Arguments> getCommonUserAndAnonymousUserArgs() {
        return java.util.stream.Stream.of(
                arguments(getCommonUser()),
                arguments(getAnonymousUser())
        );
    }

    private void verifyMetadataDTo(MetadataDto dto) {
        assertEquals(LOCATION_ID_1, dto.getLocationId());
        assertEquals(DEPARTMENT_1, dto.getDepartmentId());
        assertEquals(CATEGORY_1, dto.getCategoryId());
        assertEquals(SUB_CATEGORY_1, dto.getSubCategoryId());
    }
}
