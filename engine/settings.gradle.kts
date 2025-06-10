rootProject.name = "sibatcore-mrq"

pluginManagement {
    plugins {
        id("io.spring.dependency-management") version "1.1.7"
        id("org.springframework.boot") version "3.5.0"
    }
}

include("compiler", "engine", "model")
