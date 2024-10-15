package org.mikhailov.dm.eventmanager.locations;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;
    private final LocationDtoConverter locationDtoConverter;

    @Autowired
    public LocationController(LocationService locationService, LocationDtoConverter locationDtoConverter) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }


    @GetMapping("/locations")
    public List<LocationDto> getLocations() {
        log.info("GET request for all locations");
        return locationService.getLocations()
                .stream()
                .map(locationDtoConverter::toDto)
                .toList();
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("POST request for location {}", locationDto);
        Location newLocation = locationService.createLocation(
                locationDtoConverter.toDomain(locationDto)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationDtoConverter.toDto(newLocation));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.info("DELETE request for location id {}", id);
        locationService.deleteLocation(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationDto> getLocation(@PathVariable Long id) {
        log.info("GET request for location id {}", id);
        Location location = locationService.getLocation(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationDtoConverter.toDto(location));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long id,
                               @RequestBody @Valid LocationDto locationDto) {
        log.info("PUT request for location id {}", id);
        Location updatedLocation = locationService.updateLocation(id, locationDtoConverter.toDomain(locationDto));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationDtoConverter.toDto(updatedLocation));
    }
}
