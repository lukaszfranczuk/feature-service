package com.capco.interview.features.services.mappers;

import com.capco.interview.features.repositories.entities.FeatureEntity;
import com.capco.interview.features.services.model.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeatureEntityMapper {

    Feature featureEntityToFeatureModel(FeatureEntity featureEntity);

    @Mapping(target = "featureId", ignore = true)
    @Mapping(target = "users", ignore = true)
    FeatureEntity featureModelToFeatureEntity(Feature feature);
}
