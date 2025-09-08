package co.com.leronarenwino.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI configuration with correct info")
    void customOpenAPITest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertThat(openAPI).isNotNull();

        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("User Management API");
        assertThat(info.getDescription()).isEqualTo("Restful API for managing users");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("Should configure contact information correctly")
    void contactInformationTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("API Support");
        assertThat(contact.getEmail()).isEqualTo("support@example.com");
    }

    @Test
    @DisplayName("Should configure license information correctly")
    void licenseInformationTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        License license = openAPI.getInfo().getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("Should configure JWT security scheme correctly")
    void securitySchemeTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }

    @Test
    @DisplayName("Should configure global security requirement")
    void globalSecurityRequirementTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertThat(openAPI.getSecurity()).hasSize(1);

        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertThat(securityRequirement).containsKey("bearerAuth");
        assertThat(securityRequirement.get("bearerAuth")).isEmpty();
    }

    @Test
    @DisplayName("Should verify all security components are properly linked")
    void securityComponentsLinkageTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");

        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertThat(securityRequirement).containsKey("bearerAuth");

        String securitySchemeKey = openAPI.getComponents().getSecuritySchemes().keySet().iterator().next();
        String securityRequirementKey = securityRequirement.keySet().iterator().next();
        assertThat(securityRequirementKey).isEqualTo(securitySchemeKey);
    }

    @Test
    @DisplayName("Should have complete OpenAPI structure")
    void completeOpenAPIStructureTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Verificar estructura principal
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getSecurity()).isNotNull();

        // Verificar que todos los campos obligatorios están presentes
        assertThat(openAPI.getInfo().getTitle()).isNotBlank();
        assertThat(openAPI.getInfo().getVersion()).isNotBlank();
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("Should create new instance on each call")
    void newInstanceOnEachCallTest() {
        OpenAPI openAPI1 = openApiConfig.customOpenAPI();
        OpenAPI openAPI2 = openApiConfig.customOpenAPI();

        assertThat(openAPI1).isNotSameAs(openAPI2);
        assertThat(openAPI1).usingRecursiveComparison().isEqualTo(openAPI2);
    }

    @Test
    @DisplayName("Should configure HTTP scheme as bearer")
    void httpBearerSchemeTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        SecurityScheme bearerAuth = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");

        assertThat(bearerAuth.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(bearerAuth.getScheme()).isEqualToIgnoringCase("bearer");
        assertThat(bearerAuth.getBearerFormat()).isEqualToIgnoringCase("JWT");
    }

    @Test
    @DisplayName("Should have proper API version format")
    void apiVersionFormatTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        String version = openAPI.getInfo().getVersion();
        assertThat(version).matches("\\d+\\.\\d+\\.\\d+");
    }

    @Test
    @DisplayName("Should have valid email format in contact")
    void validEmailFormatTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        String email = openAPI.getInfo().getContact().getEmail();
        assertThat(email).matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    @Test
    @DisplayName("Should have valid URL format in license")
    void validLicenseUrlFormatTest() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        String licenseUrl = openAPI.getInfo().getLicense().getUrl();
        assertThat(licenseUrl).startsWith("https://")
                .contains("opensource.org");
    }

    @Test
    @DisplayName("Should verify that configuration is a Spring Bean")
    void springBeanConfigurationTest() {
        assertThat(OpenApiConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class))
                .isTrue();
    }

    @Test
    @DisplayName("Should verify customOpenAPI method is marked as Bean")
    void customOpenAPIBeanAnnotationTest() throws NoSuchMethodException {
        assertThat(OpenApiConfig.class
                .getMethod("customOpenAPI")
                .isAnnotationPresent(org.springframework.context.annotation.Bean.class))
                .isTrue();
    }
}