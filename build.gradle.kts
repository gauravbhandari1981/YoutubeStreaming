plugins {
    java
}

group = "com.airos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // We will add Kafka or JSON libraries here later
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}