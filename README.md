[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Nexus Snapshot](https://img.shields.io/nexus/s/com.uwyn/urlencoder?server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/uwyn/urlencoder/)
[![Release](https://img.shields.io/github/release/gbevin/urlencoder.svg)](https://github.com/gbevin/urlencoder/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.uwyn/urlencoder/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/com.uwyn/urlencoder)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gbevin_urlencoder&metric=alert_status)](https://sonarcloud.io/dashboard?id=gbevin_urlencoder)
[![GitHub CI](https://github.com/gbevin/urlencoder/actions/workflows/gradle.yml/badge.svg)](https://github.com/gbevin/urlencoder/actions/workflows/gradle.yml)

# URL Encoder for Java

A simple library to encode/decode URL parameters.

This library was extracted from the [RIFE2 Web Application Framework](https://rife2.com).  
A Kotlin version can also be found at [https://github.com/ethauvin/urlencoder](https://github.com/ethauvin/urlencoder).

For decades, we've been using [java.net.URLEncoder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html)
because of its improper naming. It is actually intended to encode HTML form
parameters, not URLs, causing the wrong escape sequences to be used.

Additionally, `java.net.URLEncoder` allocates memory even when no encoding is
necessary, significantly impacting performance. This library has a negligible
performance impact when the string that is passed in doesn't need to be encoded.

Android's [Uri.encode](https://developer.android.com/reference/android/net/Uri#encode(java.lang.String,%20java.lang.String))
also addresses these issues.

## Examples (TL;DR)

```java
UrlEncoder.encode("a test &"); // -> "a%20test%20%26"
UrlEncoder.encode("%#okékÉȢ smile!😁"); // -> "%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"
UrlEncoder.encode("?test=a test", "?="); // -> ?test=a%20test

UrlEncoder.decode("a%20test%20%26"); // -> "a test &"
UrlEncoder.decode("%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"); // -> "%#okékÉȢ smile!😁"
```

## Gradle, Maven, etc.
To use with [Gradle](https://gradle.org/), include the following dependency in your build file:

```gradle
dependencies {
    implementation("com.uwyn:urlencoder:1.0.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on Maven Central.
