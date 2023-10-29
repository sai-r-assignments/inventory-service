package com.example.inventory_service.controller;

import com.example.inventory_service.config.WebSecurityConfig;
import com.example.inventory_service.domain.SkuData;
import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.service.SkuDataService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.stream.Stream;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(SkuDataController.class)
class SkuDataControllerTest {

    private static final MetadataDto METADATA_DTO = new MetadataDto(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);

    private static final SkuDataDto SKU_DTO = new SkuDataDto(SKU_1, SKU_1_DESC,
            LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkuDataService skuDataService;

    @Captor
    private ArgumentCaptor<MetadataDto> metadataDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<SkuDataDto> skuDataDtoArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getAdminAndCommonUserArgs")
    void test_get_skuData_with_admin_and_user_role(RequestPostProcessor role) throws Exception {
        List<Long> expectedResponse = List.of(1L, 2L, 3L, 4L);
        when(skuDataService.getSkuDataWithMetadata(any(MetadataDto.class)))
                .thenReturn(expectedResponse);
        mockMvc.perform(get("/api/v1/skuData")
                        .param("locationId", LOCATION_ID_1)
                        .param("departmentId", DEPARTMENT_1)
                        .param("categoryId", CATEGORY_1)
                        .param("subCategoryId", SUB_CATEGORY_1)
                        .contentType(APPLICATION_JSON)
                        .with(role))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
        verify(skuDataService).getSkuDataWithMetadata(metadataDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(skuDataService);
        verifyMetadataDTo(metadataDtoArgumentCaptor.getValue());
    }

    @Test
    void test_get_skuData_with_data_not_exists() throws Exception {
        when(skuDataService.getSkuDataWithMetadata(any(MetadataDto.class)))
                .thenThrow(new DataNotFoundException("DNF"));
        mockMvc.perform(get("/api/v1/skuData")
                        .param("locationId", LOCATION_ID_1)
                        .param("departmentId", DEPARTMENT_1)
                        .param("categoryId", CATEGORY_1)
                        .param("subCategoryId", SUB_CATEGORY_1)
                        .with(getCommonUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("DNF"));
        verify(skuDataService).getSkuDataWithMetadata(metadataDtoArgumentCaptor.capture());
        verifyNoMoreInteractions(skuDataService);
        verifyMetadataDTo(metadataDtoArgumentCaptor.getValue());
    }

    @Test
    void test_get_skuData_anonymous_role() throws Exception {
        mockMvc.perform(get("/api/v1/skuData")
                        .param("locationId", LOCATION_ID_1)
                        .param("departmentId", DEPARTMENT_1)
                        .param("categoryId", CATEGORY_1)
                        .param("subCategoryId", SUB_CATEGORY_1)
                        .with(getAnonymousUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(skuDataService);
    }

    @Test
    void test_add_skuData_for_admin_users() throws Exception {
        mockMvc.perform(post("/api/v1/skuData")
                        .content(objectMapper.writeValueAsString(SKU_DTO))
                        .contentType(APPLICATION_JSON)
                        .with(getAdminUser()))
                .andExpect(status().isCreated());
        verify(skuDataService).saveSkudata(skuDataDtoArgumentCaptor.capture(), eq(false));
        SkuDataDto dto = skuDataDtoArgumentCaptor.getValue();
        assertEquals(LOCATION_ID_1, dto.getLocationId());
        assertEquals(DEPARTMENT_1, dto.getDepartmentId());
        assertEquals(CATEGORY_1, dto.getCategoryId());
        assertEquals(SUB_CATEGORY_1, dto.getSubCategoryId());
        assertEquals(SKU_1, dto.getSku());
        assertEquals(SKU_1_DESC, dto.getName());
    }

    @ParameterizedTest
    @MethodSource("getCommonUserAndAnonymousUserArgs")
    void test_add_skuData_for_common_and_anonymous_users(RequestPostProcessor role) throws Exception {
        mockMvc.perform(post("/api/v1/skuData")
                        .content(objectMapper.writeValueAsString(SKU_DTO))
                        .contentType(APPLICATION_JSON)
                        .with(role))
                .andExpect(status().isForbidden());
        verifyNoInteractions(skuDataService);
    }

    @Test
    void test_add_skuData_with_invalid_request_data() throws Exception {
        mockMvc.perform(post("/api/v1/skuData")
                        .content(objectMapper.writeValueAsString(METADATA_DTO))
                        .contentType(APPLICATION_JSON)
                        .with(getAdminUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid request data"));
        verifyNoInteractions(skuDataService);
    }

    @Test
    void test_delete_skuData_for_admin_users() throws Exception {
        mockMvc.perform(delete("/api/v1/skuData")
                        .param("sku", String.valueOf(SKU_1))
                        .with(getAdminUser()))
                .andExpect(status().isNoContent());
        verify(skuDataService).deleteSkudata(SKU_1);
    }

    @ParameterizedTest
    @MethodSource("getCommonUserAndAnonymousUserArgs")
    void test_delete_skuData_for_common_and_anonymous_users(RequestPostProcessor role) throws Exception {
        mockMvc.perform(delete("/api/v1/skuData")
                        .param("sku", String.valueOf(SKU_1))
                        .with(role))
                .andExpect(status().isForbidden());
        verifyNoInteractions(skuDataService);
    }

    static Stream<Arguments> getAdminAndCommonUserArgs() {
        return Stream.of(
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
