package com.capco.interview.features.controllers.mappers;

import com.capco.interview.features.api.model.FeatureBody;
import com.capco.interview.features.services.model.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeatureBodyMapper {

    @Mapping(source = "id", target = "guid")
    @Mapping(target = "globallyEnabled", constant = "false")
    Feature featureBodyToFeature(FeatureBody featureBody);

    @Mapping(source = "guid", target = "id")
    FeatureBody featureToFeatureBody(Feature feature);
}
