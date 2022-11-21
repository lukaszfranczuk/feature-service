package com.capco.interview.features.controllers;

import com.capco.interview.features.api.FeaturesApi;
import com.capco.interview.features.api.model.FeatureBody;
import com.capco.interview.features.api.model.UserFeatureBody;
import com.capco.interview.features.controllers.mappers.FeatureBodyMapper;
import com.capco.interview.features.services.FeaturesService;
import com.capco.interview.features.services.model.Feature;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class FeaturesController implements FeaturesApi {

    private final FeaturesService featuresService;
    private final FeatureBodyMapper featureBodyMapper;


    @Override
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> addNewFeature(FeatureBody featureBody) {
        Feature feature = featureBodyMapper.featureBodyToFeature(featureBody);
        String featureGuid = featuresService.addNewFeature(feature);
        URI uriToCreatedResource = linkTo(FeaturesController.class).slash(featureGuid).toUri();
        return ResponseEntity
                .created(uriToCreatedResource)
                .build();
    }

    @Override
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> enableFeatureForAUser(String id, UserFeatureBody userFeatureBody) {
        featuresService.enableFeatureForAUser(id, userFeatureBody.getUserId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> globallyEnableFeature(String id) {
        featuresService.globallyEnableFeature(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<List<FeatureBody>> getAllFeaturesForLoggedInUser() {
        List<FeatureBody> features = featuresService.getFeaturesForLoggedInUser().stream()
                .map(featureBodyMapper::featureToFeatureBody)
                .collect(Collectors.toList());
        return ResponseEntity.ok(features);
    }
}
