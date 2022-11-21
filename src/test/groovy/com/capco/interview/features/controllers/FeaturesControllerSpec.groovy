package com.capco.interview.features.controllers

import com.capco.interview.features.api.model.ErrorBody
import com.capco.interview.features.api.model.FeatureBody
import com.capco.interview.features.repositories.FeaturesRepository
import com.capco.interview.features.repositories.UserRepository
import com.capco.interview.features.repositories.entities.FeatureEntity
import com.capco.interview.features.repositories.entities.UserEntity
import com.capco.interview.features.security.JwtService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

@SpringBootTest
@WebAppConfiguration
class FeaturesControllerSpec extends Specification {

    private static final String AUTHORIZATION_TOKE_PREFIX = "Bearer "

    MockMvc mockMvc

    @Autowired
    WebApplicationContext webApplicationContext

    @Autowired
    JwtService jwtService

    @Autowired
    FeaturesRepository featuresRepository

    @Autowired
    UserRepository userRepository

    ObjectMapper objectMapper

    def setup() {
        clearDatabase()
        objectMapper = new ObjectMapper()
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    def cleanup() {
        clearDatabase()
    }

    def clearDatabase() {
        featuresRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "should return 403 when request to an authorized endpoint is sent by user without an admin role"() {
        given: "JWT with USER_ROLE only"
        String jwt = jwtService.createJwt("testUser", "ROLE_USER")
        and: "mockMvc request builder for /features endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/features")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"testFeature\"}")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "request is sent to /features endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "AccessDenied http status code is returned"
        response.getStatus() == 403
        and: "correct response message is returned"
        def responseBody = objectMapper.readValue(response.getContentAsString(), ErrorBody.class)
        responseBody.getMessage() == "User doesn't have permissions to perform this action"
    }

    def "should return 201 when POST to /features endpoint is sent with correct data by user with an admin role"() {
        given: "JWT with ROLE_USER only"
        String jwt = jwtService.createJwt("testUser", "ROLE_ADMIN")
        and: "mockMvc request builder for /features endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/features")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"testFeature\"}")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "request is sent to /features endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "201 HTTP status code is returned"
        response.getStatus() == 201
        and: "new record is added to the database"
        featuresRepository.findAll().size() == 1
    }

    def "should return 422 when two POST requests with the same feature name are sent to /features endpoint"() {
        given: "JWT with ROLE_ADMIN"
        String jwt = jwtService.createJwt("testUser", "ROLE_ADMIN")
        and: "mockMvc request builder for /features endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/features")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"testFeature\"}")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "first POST request is sent to /features endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()
        and: "second POST request is sent to /features endpoint with the same body"
        def response2 = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "first request return HTTP code 201"
        response.getStatus() == 201
        and: "it creates new record in the database"
        featuresRepository.findAll().size() == 1
        and: "second request return 422 HTTP code, as feature with given name already exist"
        response2.getStatus() == 422
    }

    def "should return 204 when POST to /features/{id}/actions/enable-globally endpoint is sent for existing feature by user with an admin role"() {
        given: "JWT with ROLE_ADMIN"
        def jwt = jwtService.createJwt("testUser", "ROLE_ADMIN")
        and: "feature is saved to the database"
        def feature = new FeatureEntity()
        feature.setName("testFeature")
        feature.setGuid("testGuid")
        feature.setGloballyEnabled(false)
        def saveFeatureEntityId = featuresRepository.save(feature).getFeatureId()
        and: "mockMvc request builder for /features/testGuid/actions/enable-globally endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/features/testGuid/actions/enable-globally")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "POST request is sent to /features/testGuid/actions/enable-globally"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "204 HTTP code is returned"
        response.getStatus() == 204
        and: "it creates new record in the database"
        featuresRepository.findById(saveFeatureEntityId).get().getGloballyEnabled() == true
    }

    def "should return 204 when POST to /features/{id}/actions/enable endpoint is sent for existing feature with correct data by user with an admin role"() {
        given: "JWT with ROLE_ADMIN"
        def jwt = jwtService.createJwt("testGuid", "ROLE_ADMIN")
        and: "user is present in the database"
        def user = new UserEntity()
        user.setUsername("testUsername")
        user.setGuid("testGuid")
        user.setPassword("testPassword")
        def savedUserEntityId = userRepository.save(user).getUserId()
        and: "feature is present in the database"
        def feature = new FeatureEntity()
        feature.setName("testFeature")
        feature.setGuid("testGuid")
        feature.setGloballyEnabled(false)
        def savedFeatureEntityId = featuresRepository.save(feature).getFeatureId()
        and: "mockMvc request builder for /features/testGuid/actions/enable endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/features/testGuid/actions/enable")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"userId\":\"testGuid\"}")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "POST request is sent to /features/testGuid/actions/enable"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "204 HTTP code is returned"
        response.getStatus() == 204
        and: "a feature is assigned to the logged in user"
        def userEntity = userRepository.findById(savedUserEntityId).get()
        def features = userEntity.getFeatures()
        features.get(0).getFeatureId() == savedFeatureEntityId
    }


    def "should return 200 when GET request to /features endpoint is sent"() {
        given: "JWT with ROLE_USER"
        def jwt = jwtService.createJwt("testGuid", "ROLE_USER")
        and: "user is present in the database"
        def user = new UserEntity()
        user.setUsername("testUsername")
        user.setGuid("testGuid")
        user.setPassword("testPassword")
        def savedUserEntity = userRepository.save(user)
        and: "feature assigned to the user is present in the database"
        def feature = new FeatureEntity()
        feature.setName("testFeature")
        feature.setGuid("testGuid")
        feature.setGloballyEnabled(false)
        feature.getUsers().add(savedUserEntity)
        featuresRepository.save(feature)
        and: "feature globally enabled is present in the database"
        def feature2 = new FeatureEntity()
        feature2.setName("testFeature2")
        feature2.setGuid("testGuid2")
        feature2.setGloballyEnabled(true)
        featuresRepository.save(feature2)
        and: "mockMvc request builder for /features endpoint"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/features")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "GET request is sent to /features"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "200 HTTP code is returned"
        response.getStatus() == 200
        and: "list of features is returned"
        def featuresFromApi = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FeatureBody>>() {
        })
        featuresFromApi.size() == 2
        featuresFromApi.get(0).getName() == "testFeature"
        featuresFromApi.get(1).getName() == "testFeature2"
    }


}
