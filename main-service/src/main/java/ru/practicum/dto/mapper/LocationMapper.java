package ru.practicum.dto.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocationDto(LocationDto dto);

    LocationDto toLocation(Location location);
}
