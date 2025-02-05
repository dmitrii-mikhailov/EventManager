package org.mikhailov.dm.eventmanager.locations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository,
                           LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(locationEntityConverter::toDomain)
                .toList();
    }

    public Location createLocation(Location location) {
        LocationEntity locationEntity = new LocationEntity(
                null,
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );

        LocationEntity savedEntity =
                locationRepository.save(locationEntity);

        return locationEntityConverter.toDomain(savedEntity);
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("No location found with id: " + id);
        }

        locationRepository.deleteById(id);
    }

    public Location getLocation(Long id) {
        return locationEntityConverter.toDomain(locationRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("No location found with id: " + id)));
    }

    public Location updateLocation(Long id, Location location) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("No location found with id: " + id);
        }
        LocationEntity locationEntity = locationEntityConverter.toEntity(location);
        locationEntity.setId(id);

        return locationEntityConverter.toDomain(locationRepository.save(locationEntity));
    }

    public boolean locationExists(Long id) {
        return locationRepository.existsById(id);
    }
}