package com.capco.interview.features.services;

import com.capco.interview.features.exceptions.FeatureAlreadyExistException;
import com.capco.interview.features.exceptions.FeatureNotExistException;
import com.capco.interview.features.exceptions.UserNotExistException;
import com.capco.interview.features.repositories.FeaturesRepository;
import com.capco.interview.features.repositories.UserRepository;
import com.capco.interview.features.repositories.entities.FeatureEntity;
import com.capco.interview.features.repositories.entities.UserEntity;
import com.capco.interview.features.services.mappers.FeatureEntityMapper;
import com.capco.interview.features.services.model.Feature;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeaturesService {

    private final FeaturesRepository featuresRepository;
    private final UserRepository userRepository;
    private final FeatureEntityMapper featureEntityMapper;

    public String addNewFeature(Feature feature) {
        Optional<FeatureEntity> featureByName = featuresRepository.findByName(feature.getName());
        if (featureByName.isPresent()) {
            throw new FeatureAlreadyExistException("Feature with name " + feature.getName() + " already exist");
        }
        String guid = UUID.randomUUID().toString();
        FeatureEntity featureEntity = featureEntityMapper.featureModelToFeatureEntity(feature);
        featureEntity.setGuid(guid);
        FeatureEntity savedFeatureEntity = featuresRepository.save(featureEntity);
        return savedFeatureEntity.getGuid();
    }

    public List<Feature> getFeaturesForLoggedInUser() {
        UserEntity userEntity = fetchUserEntity(getLoggedInUserGuid());
        Set<FeatureEntity> userEntities = featuresRepository.findByUsers(userEntity);
        userEntities.addAll(featuresRepository.findByGloballyEnabled(true));
        return userEntities.stream()
                .map(featureEntityMapper::featureEntityToFeatureModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public void enableFeatureForAUser(String featureGuid, String userGuid) {
        FeatureEntity featureEntity = fetchFeatureEntity(featureGuid);
        UserEntity userEntity = fetchUserEntity(userGuid);
        featureEntity.getUsers().add(userEntity);
    }

    public void globallyEnableFeature(String featureGuid) {
        FeatureEntity featureEntity = fetchFeatureEntity(featureGuid);
        featureEntity.setGloballyEnabled(true);
        featuresRepository.save(featureEntity);
    }

    private FeatureEntity fetchFeatureEntity(String featureGuid) {
        return featuresRepository.findByGuid(featureGuid)
                .orElseThrow(() -> new FeatureNotExistException("Feature with id " + featureGuid + " doesn't exist"));
    }

    private UserEntity fetchUserEntity(String userGuid) {
        return userRepository.findByGuid(userGuid)
                .orElseThrow(() -> new UserNotExistException("User with id " + userGuid + " doesn't exist"));
    }

    private static String getLoggedInUserGuid() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
