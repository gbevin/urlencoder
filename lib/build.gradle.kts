plugins {
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("org.sonarqube") version "3.5.0.2730"
}

group = "com.uwyn"
version = "1.0.1-SNAPSHOT"

base {
    archivesName.set(rootProject.name)
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.uwyn.urlencoder.UrlEncoder"
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

sonarqube {
    properties {
        property("sonar.projectName", rootProject.name)
        property("sonar.projectKey", "gbevin_${rootProject.name}")
        property("sonar.organization", "gbevin")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
    }
}
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name.set("URLEncoder")
                description.set("A simple library to encode/decode URL parameters")
                url.set("https://github.com/gbevin/urlencoder")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("gbevin")
                        name.set("Geert Bevin")
                        email.set("gbevin@uwyn.com")
                        url.set("https://github.com/gbevin")
                    }
                    developer {
                        id.set("ethauvin")
                        name.set("Erik C. Thauvin")
                        email.set("erik@thauvin.net")
                        url.set("https://erik.thauvin.net/")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/gbevin/urlencoder.git")
                    developerConnection.set("scm:git:git@github.com:gbevin/urlencoder.git")
                    url.set("https://github.com/gbevin/urlencoder")
                }
            }
            repositories {
                maven {
                    credentials {
                        username = project.properties["ossrhUsername"].toString()
                        password = project.properties["ossrhPassword"].toString()
                    }
                    val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
