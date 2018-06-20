package io.fake

import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import org.testng.annotations.BeforeSuite
import java.lang.System.getProperty

class SetUpFramework {

    @BeforeSuite
    fun setUp() {
        val reqSpec = RequestSpecBuilder()
                .addFilter(AllureRestAssured())
                .setContentType("application/json")
                .setAccept("application/vnd.github.v3+json")
                .addHeader("Authorization", "token " + getProperty("token"))
                .setBaseUri("https://api.github.com")
                .build()
        RestAssured.requestSpecification = reqSpec
    }
}