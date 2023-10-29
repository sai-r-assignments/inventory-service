package com.example.inventory_service.service;

import com.example.inventory_service.domain.Location;
import com.example.inventory_service.dto.LocationDto;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    public void addNewLocation(LocationDto locationDto) {
        locationRepository.findByLocationId(locationDto.locationId())
                .ifPresent(l -> {
                    throw new DataAlreadyExistsException("Location already exists with id " + locationDto.locationId());
                });
        saveNewLocation(locationDto.locationId(), locationDto.description());
    }

    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            throw new DataNotFoundException("No locations found");
        }
        return locations.stream().map(this::convertToDto).toList();
    }

    public void updateLocation(LocationDto locationDto) {
        Location location = getLocation(locationDto.locationId());
        location.setDescription(locationDto.description());
        saveLocation(location);
    }

    public void deleteLocation(String locationId) {
        Location location = getLocation(locationId);
        try {
            locationRepository.delete(location);
        } catch (DataIntegrityViolationException e) {
            log.error("Can not delete location as metadata exists in db referring to given location", e);
            throw new InvalidOperationException("Metadata exists referring to given location. Please delete metadata with given location.");
        }
    }

    public Optional<Location> getLocationById(String locationId) {
        return locationRepository.findByLocationId(locationId);
    }

    private Location getLocation(String locationId) {
        Optional<Location> locationOpt = locationRepository.findByLocationId(locationId);
        return locationOpt.orElseThrow(() -> new DataNotFoundException("Location not found with id " + locationId));
    }

    @Transactional
    public Location saveLocationIfNotExists(String locationId) {
        Optional<Location> locationFromDb = locationRepository.findByLocationId(locationId);
        return locationFromDb.orElseGet(() -> saveNewLocation(locationId));
    }

    private Location saveNewLocation(String locationId) {
        return saveNewLocation(locationId, null);
    }

    private Location saveNewLocation(String locationId, String locationDescription) {
        Location location = new Location(locationId);
        location.setDescription(locationDescription);
        return saveLocation(location);
    }

    private Location saveLocation(Location location) {
        locationRepository.save(location);
        return location;
    }

    private LocationDto convertToDto(Location location) {
        return new LocationDto(location.getLocationId(), location.getDescription());
    }

}
