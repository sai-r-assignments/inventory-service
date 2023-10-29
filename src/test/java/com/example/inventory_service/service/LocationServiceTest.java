package com.example.inventory_service.service;

import com.example.inventory_service.domain.Location;
import com.example.inventory_service.dto.LocationDto;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    private static final LocationDto LOCATION_DTO = new LocationDto(LOCATION_ID_2, LOCATION_2_DESC);

    @Mock
    private LocationRepository locationRepository;

    @Captor
    private ArgumentCaptor<Location> locationArgumentCaptor;

    private LocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new LocationService(locationRepository);
    }

    @Test
    void add_new_location_when_given_location_not_exists() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.empty());
        locationService.addNewLocation(LOCATION_DTO);
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verify(locationRepository).save(locationArgumentCaptor.capture());
        verifyNoMoreInteractions(locationRepository);
        Location saved = locationArgumentCaptor.getValue();
        assertEquals(LOCATION_ID_2, saved.getLocationId());
        assertEquals(LOCATION_2_DESC, saved.getDescription());
    }

    @Test
    void add_new_location_throws_DataAlreadyExistsException_when_given_location_already_exists() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.of(new Location()));
        Exception e = assertThrows(DataAlreadyExistsException.class,
                () -> locationService.addNewLocation(LOCATION_DTO));
        assertTrue(e.getMessage().contains(LOCATION_ID_2));
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void get_all_locations() {
        when(locationRepository.findAll())
                .thenReturn(List.of(LOCATION_1, LOCATION_2));
        List<LocationDto> locations = locationService.getAllLocations();
        assertEquals(2, locations.size());
        assertEquals(LOCATION_ID_1, locations.get(0).locationId());
        assertNull(locations.get(0).description());
        assertEquals(LOCATION_ID_2, locations.get(1).locationId());
        assertEquals(LOCATION_2_DESC, locations.get(1).description());
        verify(locationRepository).findAll();
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void get_all_locations_throws_DataNotFoundException_when_no_locations_found() {
        when(locationRepository.findAll())
                .thenReturn(Collections.emptyList());
        assertThrows(DataNotFoundException.class,
                () -> locationService.getAllLocations());
        verify(locationRepository).findAll();
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_update_location() {
        when(locationRepository.findByLocationId(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        locationService.updateLocation(new LocationDto(LOCATION_ID_1, null));
        verify(locationRepository).findByLocationId(LOCATION_ID_1);
        verify(locationRepository).save(locationArgumentCaptor.capture());
        verifyNoMoreInteractions(locationRepository);
        Location saved = locationArgumentCaptor.getValue();
        assertEquals(LOCATION_ID_1, saved.getLocationId());
        assertNull(saved.getDescription());
    }

    @Test
    void test_update_location_throws_DataNotFoundException_when_given_location_not_found() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> locationService.updateLocation(LOCATION_DTO));
        assertTrue(e.getMessage().contains(LOCATION_ID_2));
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_delete_location() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.of(LOCATION_2));
        locationService.deleteLocation(LOCATION_ID_2);
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verify(locationRepository).delete(LOCATION_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_delete_location_throws_DataNotFoundException_when_given_location_not_found() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> locationService.deleteLocation(LOCATION_ID_2));
        assertTrue(e.getMessage().contains(LOCATION_ID_2));
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_delete_location_throws_InvalidOperationException_when_integrity_constraint_violated() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.of(LOCATION_2));
        doThrow(new DataIntegrityViolationException("DIVE"))
                .when(locationRepository).delete(LOCATION_2);
        assertThrows(InvalidOperationException.class,
                () -> locationService.deleteLocation(LOCATION_ID_2));
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verify(locationRepository).delete(LOCATION_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_get_location_by_id() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.of(LOCATION_2));
        Optional<Location> location = locationService.getLocationById(LOCATION_ID_2);
        assertTrue(location.isPresent());
        assertEquals(LOCATION_2, location.get());
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_save_location_not_exists_when_location_exists() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.of(LOCATION_2));
        assertEquals(LOCATION_2, locationService.saveLocationIfNotExists(LOCATION_ID_2));
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void test_save_location_not_exists_when_location_not_exists() {
        when(locationRepository.findByLocationId(LOCATION_ID_2))
                .thenReturn(Optional.empty());
        Location location = locationService.saveLocationIfNotExists(LOCATION_ID_2);
        verify(locationRepository).findByLocationId(LOCATION_ID_2);
        verify(locationRepository).save(location);
        verifyNoMoreInteractions(locationRepository);
        assertEquals(LOCATION_ID_2, location.getLocationId());
        assertNull(location.getDescription());
    }
}
