plugins {
    id("java-library")
    id("org.springframework.boot")
}

description = "Wrapper for calling the StandardQL compiler"

dependencies {
    compileOnlyApi("org.springframework.boot:spring-boot-starter-json")
    testImplementation("org.springframework.boot:spring-boot-starter-json")

    implementation("org.buildobjects:jproc")
    implementation("org.apache.poi:poi")
    implementation("org.apache.poi:poi-ooxml")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    compileOnlyApi("org.jetbrains:annotations")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
