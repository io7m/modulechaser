modulechaser
===

[![Travis](https://img.shields.io/travis/io7m/modulechaser.png?style=flat-square)](https://travis-ci.org/io7m/modulechaser)
[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.modulechaser/com.io7m.modulechaser.png?style=flat-square)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.modulechaser%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.io7m.modulechaser/com.io7m.modulechaser.png?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/io7m/modulechaser/)

![modulechaser](./src/site/resources/modulechaser.jpg?raw=true)

Requirements
===

* Maven 3.5.0
* JDK 9+

Usage
===

Create a `pom.xml` file and insert into it all of the dependencies
you wish to analyze. The plugin doesn't care what `groupId`, `artifactId`,
etc, you use. It only looks at the (transitive) dependencies of the
project:

```
<?xml version="1.0" encoding="UTF-8"?>
<project
  xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>
  <groupId>com.io7m.modulechaser.tests</groupId>
  <artifactId>testproject</artifactId>
  <version>0.1.0</version>
  <packaging>jar</packaging>

  <dependencies>
    <dependency>
      <groupId>com.google.auto.value</groupId>
      <artifactId>auto-value</artifactId>
      <version>1.5.3</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.ow2.asm</groupId>
      <artifactId>asm</artifactId>
      <version>6.1-beta2</version>
    </dependency>
    <dependency>
      <groupId>org.ow2.asm</groupId>
      <artifactId>asm-tree</artifactId>
      <version>6.1-beta2</version>
    </dependency>
    <dependency>
      <groupId>com.beust</groupId>
      <artifactId>jcommander</artifactId>
      <version>1.72</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.3.0-alpha3</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <version>5.0.0</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <version>5.0.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

</project>
```

Then run the plugin's `chaseModules` goal from the command line:

```
$ mvn com.io7m.modulechaser:com.io7m.modulechaser:0.0.1:chaseModules
```

The plugin will generate an XHTML report in `target/modulechaser/modules.xhtml`.
