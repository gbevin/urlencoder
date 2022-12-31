[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Nexus Snapshot](https://img.shields.io/nexus/s/com.uwyn/urlencoder?server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/uwyn/urlencoder/)
[![GitHub CI](https://github.com/gbevin/urlencoder/actions/workflows/gradle.yml/badge.svg)](https://github.com/gbevin/urlencoder/actions/workflows/gradle.yml)

# URL Encoder for Java

A simple library to encode/decode URL parameters.

This library was extracted from the [RIFE2 Web Application Framework](https://rife2.com).  
A Kotlin version can also be found at [https://github.com/ethauvin/urlencoder](https://github.com/ethauvin/urlencoder).

For decades, we've been using [java.net.URLEncoder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLEncoder.html)
because of its improper naming. It is actually intended to encode HTML form
parameters, not URLs.

Android's [Uri.encode](https://developer.android.com/reference/android/net/Uri#encode(java.lang.String,%20java.lang.String))
also addresses this issue.

## Examples (TL;DR)

```java
UrlEncoder.encode("a test &"); // -> "a%20test%20%26"
UrlEncoder.encode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~"); // -> "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~"
UrlEncoder.encode("%#okékÉȢ smile!😁"); // -> "%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"
UrlEncoder.encode("?test=a test", "?="); // -> ?test=a%20test

UrlEncoder.decode("a%20test%20%26"); // -> "a test &"
UrlEncoder.decode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~"); // -> "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~"
UrlEncoder.decode("%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"); // -> "%#okékÉȢ smile!😁"
```

## Gradle, Maven, etc.
To use with [Gradle](https://gradle.org/), include the following dependency in your build file:

```gradle
dependencies {
    implementation("com.uwyn:urlencoder:0.9-SNAPSHOT")
}
```

Instructions for using with Maven, Ivy, etc. can be found on Maven Central.
