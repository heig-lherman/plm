plugins {
    id("java-library")
    id("org.springframework.boot")
}

description = "Model classes for interacting with the StandardQL compiler and engine"

dependencies {
    compileOnlyApi("org.jetbrains:annotations")

    implementation("com.google.guava:guava")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
