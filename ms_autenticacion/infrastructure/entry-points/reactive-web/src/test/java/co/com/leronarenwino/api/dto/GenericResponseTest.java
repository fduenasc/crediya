package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class GenericResponseTest {

    @Test
    @DisplayName("Should create a successful response with data")
    void successWithDataTest() {
        String testData = "test data";
        String testMessage = "Operation successful";

        GenericResponse<String> response = GenericResponse.success(testData, testMessage);

        assertThat(response).isNotNull();
        assertThat(response.data()).isEqualTo(testData);
        assertThat(response.message()).isEqualTo(testMessage);
    }

    @Test
    @DisplayName("Should create a successful response without data")
    void successWithoutDataTest() {
        String testMessage = "Operation successful";

        GenericResponse<Object> response = GenericResponse.success(null, testMessage);

        assertThat(response).isNotNull();
        assertThat(response.data()).isNull();
        assertThat(response.message()).isEqualTo(testMessage);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "Custom message"})
    @DisplayName("Should create responses with different messages")
    void successWithDifferentMessagesTest(String message) {
        Object testData = new Object();

        GenericResponse<Object> response = GenericResponse.success(testData, message);

        assertThat(response.data()).isEqualTo(testData);
        assertThat(response.message()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should create a successful response with a complex object")
    void successWithComplexObjectTest() {
        ComplexTestObject testObject = new ComplexTestObject("test", 123);
        String message = "Complex object created";

        GenericResponse<ComplexTestObject> response = GenericResponse.success(testObject, message);

        assertThat(response.data()).isEqualTo(testObject);
        assertThat(response.data().name()).isEqualTo("test");
        assertThat(response.data().value()).isEqualTo(123);
        assertThat(response.message()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should verify equals and hashCode")
    void equalsAndHashCodeTest() {
        String data = "test";
        String message = "message";

        GenericResponse<String> response1 = GenericResponse.success(data, message);
        GenericResponse<String> response2 = GenericResponse.success(data, message);
        GenericResponse<String> response3 = GenericResponse.success("different", message);

        assertThat(response1)
                .isEqualTo(response2)
                .isNotEqualTo(response3)
                .hasSameHashCodeAs(response2)
                .doesNotHaveSameHashCodeAs(response3);
    }

    @Test
    @DisplayName("Should verify toString")
    void toStringTest() {
        String data = "test data";
        String message = "test message";

        GenericResponse<String> response = GenericResponse.success(data, message);

        assertThat(response.toString())
                .contains("GenericResponse")
                .contains(data)
                .contains(message);
    }

    @Test
    @DisplayName("Should handle different generic types")
    void differentGenericTypesTest() {
        GenericResponse<Integer> intResponse = GenericResponse.success(42, "Integer response");
        GenericResponse<Boolean> boolResponse = GenericResponse.success(true, "Boolean response");

        assertThat(intResponse.data()).isEqualTo(42);
        assertThat(intResponse.data()).isInstanceOf(Integer.class);

        assertThat(boolResponse.data()).isTrue();
    }

    @Test
    @DisplayName("Should be immutable")
    void immutabilityTest() {
        GenericResponse<String> response = GenericResponse.success("test", "message");

        assertThat(response.getClass().isRecord()).isTrue();

        String originalData = response.data();
        String originalMessage = response.message();

        assertThat(response.data()).isEqualTo(originalData);
        assertThat(response.message()).isEqualTo(originalMessage);
    }

    private record ComplexTestObject(String name, int value) {}
}