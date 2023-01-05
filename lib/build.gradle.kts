import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.*
import java.net.http.*

plugins {
    application
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("org.sonarqube") version "3.5.0.2730"
}

group = "com.uwyn"
version = "1.2.0"

val mavenName = "UrlEncoder"
val javaMainClass = "$group.${rootProject.name}.$mavenName"

base {
    archivesName.set(rootProject.name)
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set(javaMainClass)
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

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = javaMainClass
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
        addTestListener(object : TestListener {
            override fun beforeTest(p0: TestDescriptor?) = Unit
            override fun beforeSuite(p0: TestDescriptor?) = Unit
            override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
            override fun afterSuite(desc: TestDescriptor, result: TestResult) {
                if (desc.parent == null) {
                    val passed = result.successfulTestCount
                    val failed = result.failedTestCount
                    val skipped = result.skippedTestCount

                    if (project.properties["testsBadgeApiKey"] != null) {
                        val apiKey = project.properties["testsBadgeApiKey"]
                        val response: java.net.http.HttpResponse<String> = HttpClient.newHttpClient()
                            .send(
                                HttpRequest.newBuilder()
                                    .uri(
                                        URI(
                                            "https://rife2.com/tests-badge/update/com.uwyn/urlencoder?" +
                                                    "apiKey=$apiKey&" +
                                                    "passed=$passed&" +
                                                    "failed=$failed&" +
                                                    "skipped=$skipped"
                                        )
                                    )
                                    .POST(HttpRequest.BodyPublishers.noBody())
                                    .build(), HttpResponse.BodyHandlers.ofString()
                            )
                        println("RESPONSE: " + response.statusCode())
                        println(response.body())
                    }
                }
            }
        })
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name.set(mavenName)
                description.set("A simple defensive library to encode/decode URL components")
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
