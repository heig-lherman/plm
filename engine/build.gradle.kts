plugins {
    id("io.spring.dependency-management")
}

allprojects {
    group = "ch.vd.ptep"
}

subprojects {
    version = "1.0.0-SNAPSHOT"

    apply(plugin = "io.spring.dependency-management")

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
        }

        dependencies {
            // Compiler
            dependency("org.buildobjects:jproc:2.8.2")
            dependency("org.apache.poi:poi:5.4.1")
            dependency("org.apache.poi:poi-ooxml:5.4.1")

            // Utilities
            dependency("org.jetbrains:annotations:26.0.2")
            dependency("com.google.guava:guava:33.4.8-jre")
            dependency("org.projectlombok:lombok:1.18.38")
            dependency("org.mapstruct:mapstruct:1.6.3")
            dependency("org.mapstruct:mapstruct-processor:1.6.3")
            dependency("org.mapstruct.extensions.spring:mapstruct-spring-annotations:1.1.3")
            dependency("org.mapstruct.extensions.spring:mapstruct-spring-extensions:1.1.3")
        }
    }
}
