package com.capco.interview.features.controllers.mappers

import com.capco.interview.features.api.model.FeatureBody
import com.capco.interview.features.services.model.Feature
import spock.lang.Specification

class FeatureBodyMapperSpec extends Specification {

    FeatureBodyMapper featureBodyMapper = new FeatureBodyMapperImpl()

    def "should map Feature model object to FeatureBody object"() {
        given: "Feature model object"
        def feature = new Feature()
        feature.setName("testName")
        feature.setGuid("testGuid")
        feature.setGloballyEnabled(false)

        when: "mapping method is executed"
        def featureBody = featureBodyMapper.featureToFeatureBody(feature)

        then: "featureBody should have id mapped from guid and name should be the same"
        featureBody.getId() == feature.getGuid()
        featureBody.getName() == feature.getName()
    }

    def "should map FeatureBody model object to Feature object"() {
        given: "Feature model object"
        def featureBody = new FeatureBody()
        featureBody.setId("testId")
        featureBody.setName("testName")

        when: "mapping method is executed"
        def feature = featureBodyMapper.featureBodyToFeature(featureBody)

        then: "featureBody should have id mapped from guid and name should be the same"
        feature.getGuid() == featureBody.getId()
        feature.getName() == featureBody.getName()
    }
}
