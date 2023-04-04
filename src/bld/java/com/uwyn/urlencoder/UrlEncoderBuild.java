package com.uwyn.urlencoder;

import rife.bld.Project;
import rife.bld.extension.TestsBadgeOperation;
import rife.bld.publish.PublishDeveloper;
import rife.bld.publish.PublishInfo;
import rife.bld.publish.PublishLicense;
import rife.bld.publish.PublishScm;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class UrlEncoderBuild extends Project {
    public UrlEncoderBuild() {
        pkg = "com.uwyn.urlencoder";
        name = "UrlEncoder";
        mainClass = "com.uwyn.urlencoder.UrlEncoder";
        version = version(1,3,1,"SNAPSHOT");

        publishRepository = version.isSnapshot() ? repository("sonatype-snapshots") : repository("sonatype-releases");
        publishInfo = new PublishInfo()
            .groupId("com.uwyn")
            .artifactId("urlencoder")
            .description("A simple defensive library to encode/decode URL components.")
            .url("https://github.com/rife2/tests-badge")
            .developer(new PublishDeveloper()
                .id("gbevin")
                .name("Geert Bevin")
                .email("gbevin@uwyn.com")
                .url("https://github.com/gbevin"))
            .developer(new PublishDeveloper()
                .id("ethauvin")
                .name("Erik C. Thauvin")
                .email("erik@thauvin.net")
                .url("https://erik.thauvin.net/"))
            .license(new PublishLicense()
                .name("The Apache License, Version 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
            .scm(new PublishScm()
                .connection("scm:git:https://github.com/gbevin/urlencoder.git")
                .developerConnection("scm:git:git@github.com:gbevin/urlencoder.git")
                .url("https://github.com/gbevin/urlencoder"))
            .signKey(property("sign.key"))
            .signPassphrase(property("sign.passphrase"));

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_CENTRAL, RIFE2);
        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,9,2)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,9,2)));
    }

    private final TestsBadgeOperation testsBadgeOperation = new TestsBadgeOperation();

    public void test()
    throws Exception {
        testsBadgeOperation.executeOnce(() -> testsBadgeOperation
            .url(property("testsBadgeUrl"))
            .apiKey(property("testsBadgeApiKey"))
            .fromProject(this));
    }

    public static void main(String[] args) {
        new UrlEncoderBuild().start(args);
    }
}