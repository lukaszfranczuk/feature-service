package com.capco.interview.features.services

import com.capco.interview.features.exceptions.FeatureAlreadyExistException
import com.capco.interview.features.exceptions.FeatureNotExistException
import com.capco.interview.features.repositories.FeaturesRepository
import com.capco.interview.features.repositories.UserRepository
import com.capco.interview.features.services.mappers.FeatureEntityMapper
import com.capco.interview.features.services.mappers.FeatureEntityMapperImpl
import com.capco.interview.features.services.model.Feature
import spock.lang.Specification

class FeatureServiceSpec extends Specification {

    FeatureEntityMapper featureEntityMapper = new FeatureEntityMapperImpl()
    FeaturesRepository featuresRepository = Mock()
    UserRepository userRepository = Mock()

    FeaturesService featuresService = new FeaturesService(featuresRepository, userRepository, featureEntityMapper)

    def "should save new feature to the database and return id of created object"() {
        given: "Feature model object"
        def feature = new Feature()
        feature.setName("testName")
        feature.setGloballyEnabled(false)
        featuresRepository.findByName("testName") >> Optional.empty()
        def entity = featureEntityMapper.featureModelToFeatureEntity(feature)
        entity.setGuid(UUID.randomUUID().toString())
        featuresRepository.save(_) >> entity

        when: "addNewFeature method is executed"
        def newFeatureId = featuresService.addNewFeature(feature)

        then: "newFeatureId is not null"
        newFeatureId != null
    }

    def "should throw a FeatureAlreadyExistException when feature with given name already exist"() {
        given: "Feature model object"
        def feature = new Feature()
        feature.setName("testName")
        feature.setGloballyEnabled(false)
        featuresRepository.findByName("testName") >> Optional.of(feature)

        when: "addNewFeature method is executed"
        featuresService.addNewFeature(feature)

        then: "FeatureAlreadyExistException is thrown"
        thrown FeatureAlreadyExistException
    }

    def "should throw a FeatureNotExistException when feature with given guid doesn't exist"() {
        given:
        def guid = "testGuid"
        featuresRepository.findByGuid(guid) >> Optional.empty()

        when: "globallyEnableFeature method is executed"
        featuresService.globallyEnableFeature(guid)

        then: "FeatureAlreadyExistException is thrown"
        thrown FeatureNotExistException
    }


}
