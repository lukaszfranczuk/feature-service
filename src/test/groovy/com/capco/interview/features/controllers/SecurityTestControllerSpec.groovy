package com.capco.interview.features.controllers

import com.capco.interview.features.security.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
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
class SecurityTestControllerSpec extends Specification {

    private static final String EXPIRED_JWT = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMmE4YTNkMS1hMzVkLTQ2YzUtOTM2MS00MTVkN2U2MDExZDQiLCJyb2xlcyI6IiIsImlhdCI6MTY2ODk2NzUzNiwiZXhwIjoxNjY4OTY4NzM2fQ.8igz0MT2sR599HXJLX5kMliJtmCXrnTn3lVkMCeJUnQ"
    private static final String AUTHORIZATION_TOKE_PREFIX = "Bearer "

    MockMvc mockMvc

    @Autowired
    WebApplicationContext webApplicationContext

    @Autowired
    JwtService jwtService


    def setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    def "should return 401 when request without token"() {
        given: "mockMvc request builder for /security-test endpoint without Authorization header"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/security-test")

        when: "request is sent to /security-test endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "AccessDenied http status code is returned"
        response.getStatus() == 401
    }

    def "should return 401 when request with expired token"() {
        given: "mockMvc request builder for /security-test endpoint with Authorization header containing expired JWT"
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/security-test")
                .header(HttpHeaders.AUTHORIZATION, EXPIRED_JWT)

        when: "request is sent to /security-test endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "AccessDenied http status code is returned"
        response.getStatus() == 401
    }

    def "should return success response with body"() {
        given: "mockMvc request builder for /security-test endpoint with Authorization header containing valid JWT"
        String jwt = jwtService.createJwt("testUser", "testRole1,testRole2")
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/security-test")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKE_PREFIX.concat(jwt))

        when: "request is sent to /security-test endpoint"
        def response = mockMvc.perform(requestBuilder).andReturn().getResponse()

        then: "response body and success http code is returned"
        response.getStatus() == 200
        response.getContentAsString() == "Hello Secured World"
    }
}
