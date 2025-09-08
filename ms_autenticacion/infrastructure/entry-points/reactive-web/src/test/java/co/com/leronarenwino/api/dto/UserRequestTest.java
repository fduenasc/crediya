package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestTest {

    private static final String STARK_NAME = "Ned";
    private static final String STARK_LASTNAME = "Stark";
    private static final String STARK_EMAIL = "nedstark@winterfell.com";
    private static final String STARK_PASSWORD = "winterIsComing123";
    private static final Double STARK_SALARY = 1000000.0; // Lord's salary
    private static final LocalDate STARK_BIRTH_DATE = LocalDate.of(1963, 8, 23);
    private static final String STARK_ADDRESS = "Winterfell, The North";
    private static final String STARK_TELEPHONE = "+7 (0) 1234-567-890";
    private static final String STARK_ROLE = "ADMIN"; // Lord of Winterfell

    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Should create valid UserRequest with all fields")
    void validUserRequestTest() {
        UserRequest userRequest = new UserRequest(
                STARK_NAME,
                STARK_LASTNAME,
                STARK_EMAIL,
                STARK_PASSWORD,
                STARK_SALARY,
                STARK_BIRTH_DATE,
                STARK_ADDRESS,
                STARK_TELEPHONE,
                STARK_ROLE
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).isEmpty();
        assertThat(userRequest.name()).isEqualTo(STARK_NAME);
        assertThat(userRequest.lastname()).isEqualTo(STARK_LASTNAME);
        assertThat(userRequest.email()).isEqualTo(STARK_EMAIL);
        assertThat(userRequest.password()).isEqualTo(STARK_PASSWORD);
        assertThat(userRequest.baseSalary()).isEqualTo(STARK_SALARY);
        assertThat(userRequest.birthDate()).isEqualTo(STARK_BIRTH_DATE);
        assertThat(userRequest.address()).isEqualTo(STARK_ADDRESS);
        assertThat(userRequest.telephone()).isEqualTo(STARK_TELEPHONE);
        assertThat(userRequest.role()).isEqualTo(STARK_ROLE);
    }

    @Test
    @DisplayName("Should create valid UserRequest with null optional fields")
    void validUserRequestWithNullOptionalFieldsTest() {
        UserRequest userRequest = new UserRequest(
                "Jane",
                "Smith",
                "jane@example.com",
                "securePass",
                75000.0,
                null,
                null,
                null,
                "ADMIN"
        );

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).isEmpty();
        assertThat(userRequest.birthDate()).isNull();
        assertThat(userRequest.address()).isNull();
        assertThat(userRequest.telephone()).isNull();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail validation when name is null or empty")
    void invalidNameTest(String name) {
        UserRequest userRequest = createUserRequestBuilder()
                .name(name)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The name is required");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail validation when lastname is null or empty")
    void invalidLastnameTest(String lastname) {
        UserRequest userRequest = createUserRequestBuilder()
                .lastname(lastname)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The lastname is required");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail validation when email is null or empty")
    void invalidEmailNullOrEmptyTest(String email) {
        UserRequest userRequest = createUserRequestBuilder()
                .email(email)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1)
                .anyMatch(v ->
                        v.getMessage().equals("The email is required") ||
                                v.getMessage().equals("Invalid email format"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@example.com", "test.example.com", "test@.com"})
    @DisplayName("Should fail validation when email format is invalid")
    void invalidEmailFormatTest(String email) {
        UserRequest userRequest = createUserRequestBuilder()
                .email(email)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "user.name@domain.co.uk", "test123@test-domain.org"})
    @DisplayName("Should validate correct email formats")
    void validEmailFormatTest(String email) {
        UserRequest userRequest = createUserRequestBuilder()
                .email(email)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail validation when password is null or empty")
    void invalidPasswordTest(String password) {
        UserRequest userRequest = createUserRequestBuilder()
                .password(password)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The password is required");
    }

    @Test
    @DisplayName("Should fail validation when baseSalary is null")
    void invalidBaseSalaryTest() {
        UserRequest userRequest = createUserRequestBuilder()
                .baseSalary(null)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The baseSalary is required");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should fail validation when role is null or empty")
    void invalidRoleTest(String role) {
        UserRequest userRequest = createUserRequestBuilder()
                .role(role)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The role is required");
    }

    @Test
    @DisplayName("Should convert to domain object")
    void toDomainTest() {
        UserRequest userRequest = new UserRequest(
                "Alice",
                "Johnson",
                "alice@example.com",
                "myPassword",
                60000.0,
                LocalDate.of(1985, 5, 15),
                "456 Oak Ave",
                "555-9876",
                "ADVISOR"
        );

        User user = userRequest.toDomain();

        assertThat(user).isNotNull();
        assertThat(user.name()).isEqualTo("Alice");
        assertThat(user.lastname()).isEqualTo("Johnson");
        assertThat(user.email()).isEqualTo("alice@example.com");
        assertThat(user.password()).isEqualTo("myPassword");
        assertThat(user.baseSalary()).isEqualTo(60000.0);
        assertThat(user.birthDate()).isEqualTo(LocalDate.of(1985, 5, 15));
        assertThat(user.address()).isEqualTo("456 Oak Ave");
        assertThat(user.telephone()).isEqualTo("555-9876");
        assertThat(user.role()).isEqualTo("ADVISOR");
    }

    @Test
    @DisplayName("Should convert to domain object with null optional fields")
    void toDomainWithNullOptionalFieldsTest() {
        UserRequest userRequest = new UserRequest(
                "Bob",
                "Wilson",
                "bob@example.com",
                "password",
                45000.0,
                null,
                null,
                null,
                "CLIENT"
        );

        User user = userRequest.toDomain();

        assertThat(user).isNotNull();
        assertThat(user.name()).isEqualTo("Bob");
        assertThat(user.lastname()).isEqualTo("Wilson");
        assertThat(user.email()).isEqualTo("bob@example.com");
        assertThat(user.password()).isEqualTo("password");
        assertThat(user.baseSalary()).isEqualTo(45000.0);
        assertThat(user.birthDate()).isNull();
        assertThat(user.address()).isNull();
        assertThat(user.telephone()).isNull();
        assertThat(user.role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("Should verify equals and hashCode")
    void equalsAndHashCodeTest() {
        UserRequest userRequest1 = createValidUserRequest();
        UserRequest userRequest2 = createValidUserRequest();
        UserRequest userRequest3 = createUserRequestBuilder()
                .name("Different")
                .build();

        assertThat(userRequest1).isEqualTo(userRequest2)
                .isNotEqualTo(userRequest3);
        assertThat(userRequest1.hashCode()).hasSameHashCodeAs(userRequest2.hashCode())
                .hasSameClassAs(userRequest3.hashCode());
    }

    @Test
    @DisplayName("Should have a meaningful toString implementation")
    void toStringTest() {
        UserRequest userRequest = createValidUserRequest();

        String toString = userRequest.toString();

        assertThat(toString).contains("UserRequest")
                .contains(STARK_NAME)
                .contains(STARK_LASTNAME)
                .contains(STARK_EMAIL)
                .contains(STARK_ROLE);
    }

    @Test
    @DisplayName("Should be immutable")
    void immutabilityTest() {
        UserRequest userRequest = createValidUserRequest();

        assertThat(userRequest.getClass().isRecord()).isTrue();

        String originalName = userRequest.name();
        String originalEmail = userRequest.email();

        assertThat(userRequest.name()).isEqualTo(originalName);
        assertThat(userRequest.email()).isEqualTo(originalEmail);
    }

    @Test
    @DisplayName("Should handle baseSalary with decimal values")
    void baseSalaryWithDecimalsTest() {
        UserRequest userRequest = createUserRequestBuilder()
                .baseSalary(50000.99)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).isEmpty();
        assertThat(userRequest.baseSalary()).isEqualTo(50000.99);

        User user = userRequest.toDomain();
        assertThat(user.baseSalary()).isEqualTo(50000.99);
    }

    @Test
    @DisplayName("Should handle extreme birthDate values")
    void extremeDatesTest() {
        LocalDate futureDate = LocalDate.of(2100, 12, 31);
        LocalDate pastDate = LocalDate.of(1900, 1, 1);

        UserRequest futureDateRequest = createUserRequestBuilder()
                .birthDate(futureDate)
                .build();

        UserRequest pastDateRequest = createUserRequestBuilder()
                .birthDate(pastDate)
                .build();

        Set<ConstraintViolation<UserRequest>> futureViolations = validator.validate(futureDateRequest);
        Set<ConstraintViolation<UserRequest>> pastViolations = validator.validate(pastDateRequest);

        assertThat(futureViolations).isEmpty();
        assertThat(pastViolations).isEmpty();
        assertThat(futureDateRequest.birthDate()).isEqualTo(futureDate);
        assertThat(pastDateRequest.birthDate()).isEqualTo(pastDate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CLIENT", "ADMIN", "ADVISOR", "CUSTOM_ROLE"})
    @DisplayName("Should handle different role values")
    void differentRolesTest(String role) {
        UserRequest userRequest = createUserRequestBuilder()
                .role(role)
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertThat(violations).isEmpty();
        assertThat(userRequest.role()).isEqualTo(role);

        User user = userRequest.toDomain();
        assertThat(user.role()).isEqualTo(role);
    }

    private UserRequest createValidUserRequest() {
        return new UserRequest(
                STARK_NAME,
                STARK_LASTNAME,
                STARK_EMAIL,
                STARK_PASSWORD,
                STARK_SALARY,
                STARK_BIRTH_DATE,
                STARK_ADDRESS,
                STARK_TELEPHONE,
                STARK_ROLE
        );
    }

    private UserRequestBuilder createUserRequestBuilder() {
        return new UserRequestBuilder()
                .name(STARK_NAME)
                .lastname(STARK_LASTNAME)
                .email(STARK_EMAIL)
                .password(STARK_PASSWORD)
                .baseSalary(STARK_SALARY)
                .birthDate(STARK_BIRTH_DATE)
                .address(STARK_ADDRESS)
                .telephone(STARK_TELEPHONE)
                .role(STARK_ROLE);
    }

    private static class UserRequestBuilder {
        private String name = STARK_NAME;
        private String lastname = STARK_LASTNAME;
        private String email = STARK_EMAIL;
        private String password = STARK_PASSWORD;
        private Double baseSalary = STARK_SALARY;
        private LocalDate birthDate = STARK_BIRTH_DATE;
        private String address = STARK_ADDRESS;
        private String telephone = STARK_TELEPHONE;
        private String role = STARK_ROLE;

        public UserRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserRequestBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public UserRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserRequestBuilder baseSalary(Double baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public UserRequestBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UserRequestBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserRequestBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public UserRequestBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserRequest build() {
            return new UserRequest(name, lastname, email, password, baseSalary,
                    birthDate, address, telephone, role);
        }
    }
}