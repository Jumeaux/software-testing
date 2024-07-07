package com.amigoscode.testing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PhoneNumberValidatorTest {
   
    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp(){
        underTest= new PhoneNumberValidator();
    }


    @ParameterizedTest
    @CsvSource({
        "+224600000000, true",
        "+2246000000009,false",
        "224600000000,false"
    })
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expectd){

        //When 
       boolean isValid= underTest.test(phoneNumber);

       // Then 
       assertThat(isValid).isEqualTo(expectd);

    }

    /* @Test
    @DisplayName("Should fail when length bigger than 13")
    void itShouldNotValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan13(){

        // Given
        String phoneNumber="+2246000000009";

        //When 
       boolean isValid= underTest.test(phoneNumber);

       // Then 
       assertThat(isValid).isFalse();

    }



    @Test
    @DisplayName("Should fail when does not start with +")
    void itShouldNotValidatePhoneNumberWhenDoesNotStratWithPlusSign(){

        // Given
        String phoneNumber="224600000000";

        //When 
       boolean isValid= underTest.test(phoneNumber);

       // Then 
       assertThat(isValid).isFalse();

    } */
}
