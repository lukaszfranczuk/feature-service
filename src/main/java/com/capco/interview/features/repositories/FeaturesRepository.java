package com.capco.interview.features.repositories;

import com.capco.interview.features.repositories.entities.FeatureEntity;
import com.capco.interview.features.repositories.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FeaturesRepository extends JpaRepository<FeatureEntity, Long> {

    Optional<FeatureEntity> findByName(String name);

    Optional<FeatureEntity> findByGuid(String guid);

    Set<FeatureEntity> findByGloballyEnabled(boolean globallyEnabled);

    Set<FeatureEntity> findByUsers(UserEntity userEntity);

}
