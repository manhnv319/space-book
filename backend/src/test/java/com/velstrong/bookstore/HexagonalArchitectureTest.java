package com.velstrong.bookstore;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    private static final String ROOT = "com.velstrong.bookstore";
    private static final String DOMAIN = "com.velstrong.bookstore.domain..";
    private static final String APPLICATION = "com.velstrong.bookstore.application..";
    private static final String INFRASTRUCTURE = "com.velstrong.bookstore.infrastructure..";
    private static final String DOMAIN_MODEL = "..domain.model..";
    private static final String DOMAIN_PORT = "..domain.port..";
    private static final String DOMAIN_SERVICE = "..domain.service..";
    private static final String ADAPTER_IN_REST = "..adapter.in.rest..";
    private static final String ADAPTER_OUT = "..adapter.out..";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(ROOT);

    @Test
    void domainMustNotDependOnSpringOrJPA() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..");
        rule.check(CLASSES);
    }

    @Test
    void domainMustNotDependOnInfrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN_MODEL)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE);
        rule.check(CLASSES);
    }

    @Test
    void domainMustNotDependOnApplication() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN_MODEL)
                .should().dependOnClassesThat().resideInAPackage(APPLICATION);
        rule.check(CLASSES);
    }

    @Test
    void domainModelClassesMustNotBeAnnotatedWithSpringStereoTypes() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN_MODEL)
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository");
        rule.check(CLASSES);
    }

    @Test
    void domainModelMustNotBeAnnotatedWithJPAEntity() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN_MODEL)
                .should().beAnnotatedWith("jakarta.persistence.Entity");
        rule.check(CLASSES);
    }

    @Test
    void restControllersMustDependOnlyOnUseCaseInterfaces() {
        ArchRule rule = noClasses().that().resideInAPackage(ADAPTER_IN_REST)
                .should().dependOnClassesThat().resideInAPackage(DOMAIN_SERVICE);
        rule.check(CLASSES);
    }

    @Test
    void applicationServicesMustNotDependOnInfrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE);
        rule.check(CLASSES);
    }

    @Test
    void persistenceAdaptersMustImplementAPort() {
        ArchRule rule = classes().that().haveSimpleNameEndingWith("PersistenceAdapter")
                .should().implement(JavaClass.Predicates.resideInAPackage(DOMAIN_PORT));
        rule.check(CLASSES);
    }

    @Test
    void outPortsMustHaveAnAdapterImplementation() {
        // Soft cross-check: at least one class per port package exists in adapter.out.
        ArchCondition<JavaClass> hasAdapter = new ArchCondition<>(
                "have at least one class in the adapter.out package") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean found = false;
                for (JavaClass candidate : CLASSES) {
                    if (!candidate.getPackageName().contains(".adapter.out.")) continue;
                    if (candidate.isInterface()) continue;
                    String name = candidate.getName();
                    if (name.startsWith(item.getName().replace(".domain.port.out.", ".infrastructure.adapter.out."))) {
                        found = true;
                        break;
                    }
                }
                String message = item.getName() + " has no implementation in " + ADAPTER_OUT;
                events.add(new com.tngtech.archunit.lang.SimpleConditionEvent(
                        item, !found, message));
            }
        };
        ArchRule rule = classes().that().resideInAPackage(DOMAIN_PORT)
                .and().areInterfaces()
                .should(hasAdapter);
        rule.check(CLASSES);
    }

    @Test
    void businessControllerMappingsMustNotEncodeRoles() throws IOException {
        List<Path> controllers = Files.walk(Path.of("src/main/java/com/velstrong/bookstore/infrastructure/adapter/in/rest"))
                .filter(path -> path.toString().endsWith("Controller.java"))
                .toList();
        List<String> roleEncodedSegments = List.of("/admin", "/staff", "/warehouse", "/manager", "/customer");

        for (Path controller : controllers) {
            List<String> lines = Files.readAllLines(controller);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.contains("Mapping(")) continue;

                assertThat(roleEncodedSegments)
                        .as(controller + " mapping must not encode actor roles: " + line.trim())
                        .noneMatch(line::contains);
            }

            assertThat(lines)
                    .as(controller + " must use permission authorities instead of role checks")
                    .noneMatch(line -> line.contains("has" + "Role("));

            assertThat(lines)
                    .as(controller + " must keep static endpoint permissions in security-endpoints.yml")
                    .noneMatch(line -> line.contains("@PreAuthorize") && line.contains("has" + "Authority("));
        }
    }

    @Test
    void endpointSecurityYamlMustNotUseRoleBasedFields() throws IOException {
        String yaml = Files.readString(Path.of("src/main/resources/security-endpoints.yml"));

        assertThat(yaml)
                .doesNotContain("role:")
                .doesNotContain("scope:")
                .doesNotContain("merge:");
    }
}
