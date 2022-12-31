# URL Encoder for Java

A simple library to encode/decode URL parameters.

This library was extracted from the [RIFE2 Web Application Framework](https://rife2.com).  
The Kotlin version can be found at [https://github.com/ethauvin/urlencoder](https://github.com/ethauvin/urlencoder).

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
