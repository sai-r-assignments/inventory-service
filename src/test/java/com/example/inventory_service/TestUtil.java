package com.example.inventory_service;

import com.example.inventory_service.domain.InventoryDetails;
import com.example.inventory_service.domain.Location;
import com.example.inventory_service.domain.Metadata;
import com.example.inventory_service.domain.SkuData;
import com.example.inventory_service.dto.LocationDto;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.stream.Stream;

public class TestUtil {

    public static final String LOCATION_ID_1 = "LOC1";
    public static final String LOCATION_ID_2 = "LOC2";
    public static final String LOCATION_2_DESC = "LOC2_DESC";
    public static final String DEPARTMENT_1 = "DEPT1";
    public static final String DEPARTMENT_2 = "DEPT2";
    public static final String DEPARTMENT_3 = "DEPT3";
    public static final String CATEGORY_1 = "DEPT1_CAT1";
    public static final String CATEGORY_2 = "DEPT1_CAT2";
    public static final String CATEGORY_3 = "DEPT2_CAT3";
    public static final String CATEGORY_4 = "DEPT3_CAT4";
    public static final String SUB_CATEGORY_1 = "DEPT1_CAT1_SUB1";
    public static final String SUB_CATEGORY_2 = "DEPT1_CAT1_SUB2";
    public static final String SUB_CATEGORY_3 = "DEPT1_CAT2_SUB3";
    public static final String SUB_CATEGORY_4 = "DEPT2_CAT3_SUB4";
    public static final String SUB_CATEGORY_5 = "DEPT2_CAT3_SUB5";
    public static final String SUB_CATEGORY_6 = "DEPT3_CAT4_SUB6";
    public static final String SUB_CATEGORY_7 = "DEPT3_CAT4_SUB7";
    public static final Long SKU_1 = 1L;
    public static final String SKU_1_DESC = "SKU_1_DESC";
    public static final Long SKU_2 = 2L;
    public static final String SKU_2_DESC = "SKU_2_DESC";
    public static final Long SKU_3 = 3L;
    public static final String SKU_3_DESC = "SKU_3_DESC";

    public static final InventoryDetails INV_DET_1 = new InventoryDetails(DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    public static final InventoryDetails INV_DET_2 = new InventoryDetails(DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_2);
    public static final InventoryDetails INV_DET_3 = new InventoryDetails(DEPARTMENT_1, CATEGORY_2, SUB_CATEGORY_3);
    public static final InventoryDetails INV_DET_4 = new InventoryDetails(DEPARTMENT_2, CATEGORY_3, SUB_CATEGORY_4);
    public static final InventoryDetails INV_DET_5 = new InventoryDetails(DEPARTMENT_2, CATEGORY_3, SUB_CATEGORY_5);
    public static final InventoryDetails INV_DET_6 = new InventoryDetails(DEPARTMENT_3, CATEGORY_4, SUB_CATEGORY_6);
    public static final InventoryDetails INV_DET_7 = new InventoryDetails(DEPARTMENT_3, CATEGORY_4, SUB_CATEGORY_7);

    public static final Location LOCATION_1 = new Location(LOCATION_ID_1);
    public static final Location LOCATION_2 = new Location(LOCATION_ID_2, LOCATION_2_DESC);
    public static final LocationDto LOCATION_DTO_1 = new LocationDto(LOCATION_ID_1, null);
    public static final LocationDto LOCATION_DTO_2 = new LocationDto(LOCATION_ID_2, LOCATION_2_DESC);

    public static final Metadata METADATA_1 = new Metadata();
    public static final Metadata METADATA_2 = new Metadata();
    public static final Metadata METADATA_3 = new Metadata();
    public static final Metadata METADATA_4 = new Metadata();
    public static final Metadata METADATA_5 = new Metadata();
    public static final Metadata METADATA_6 = new Metadata();
    public static final Metadata METADATA_7 = new Metadata();
    public static final SkuData SKU_DATA_1 = new SkuData();
    public static final SkuData SKU_DATA_2 = new SkuData();
    public static final SkuData SKU_DATA_3 = new SkuData();

    public static final List<Metadata> METADATA_LOC_1 = List.of(METADATA_1, METADATA_2, METADATA_3, METADATA_4, METADATA_5);

    static {
        METADATA_1.setLocation(LOCATION_1);
        METADATA_1.setInventoryDetails(INV_DET_1);

        METADATA_2.setLocation(LOCATION_1);
        METADATA_2.setInventoryDetails(INV_DET_2);

        METADATA_3.setLocation(LOCATION_1);
        METADATA_3.setInventoryDetails(INV_DET_3);

        METADATA_4.setLocation(LOCATION_1);
        METADATA_4.setInventoryDetails(INV_DET_4);

        METADATA_5.setLocation(LOCATION_1);
        METADATA_5.setInventoryDetails(INV_DET_5);

        METADATA_6.setLocation(LOCATION_2);
        METADATA_6.setInventoryDetails(INV_DET_6);

        METADATA_7.setLocation(LOCATION_2);
        METADATA_7.setInventoryDetails(INV_DET_7);

        SKU_DATA_1.setSku(SKU_1);
        SKU_DATA_1.setName(SKU_1_DESC);
        SKU_DATA_1.setMetadata(METADATA_1);

        SKU_DATA_2.setSku(SKU_2);
        SKU_DATA_2.setName(SKU_2_DESC);
        SKU_DATA_2.setMetadata(METADATA_1);

        SKU_DATA_3.setSku(SKU_3);
        SKU_DATA_3.setName(SKU_3_DESC);
        SKU_DATA_3.setMetadata(METADATA_7);
    }

    public static RequestPostProcessor getAdminUser() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(
                        new SimpleGrantedAuthority("SCOPE_ROLE_ADMIN"),
                        new SimpleGrantedAuthority("SCOPE_ROLE_USER")
                );
    }

    public static RequestPostProcessor getCommonUser() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(
                        new SimpleGrantedAuthority("SCOPE_ROLE_USER")
                );
    }

    public static RequestPostProcessor getAnonymousUser() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }
}
