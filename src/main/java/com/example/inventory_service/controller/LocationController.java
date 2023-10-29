package com.example.inventory_service.controller;

import com.example.inventory_service.dto.LocationDto;
import com.example.inventory_service.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void addLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Received request add new location {}", locationDto);
        locationService.addNewLocation(locationDto);
    }

    @GetMapping
    public List<LocationDto> getAllLocations() {
        log.info("Received request to get all locations");
        return locationService.getAllLocations();
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Received request to update location {}", locationDto);
        locationService.updateLocation(locationDto);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteLocation(@RequestParam @NotBlank String locationId) {
        log.info("Received request to delete locationId {}", locationId);
        locationService.deleteLocation(locationId);
    }

}
