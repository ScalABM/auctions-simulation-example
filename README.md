# Auction Simulation Example
Example application demonstrating how to run [EconomicSL](https://github.com/EconomicSL) auction simulations and analyze results using Python.

## Installing Dependencies

### Python
Recommend installing the Continuum Analytics [Anaconda](https://www.continuum.io/downloads) Python 3 distribution for your operating system.

### Java 8

To see what version (if any!) of Java you already have installed on your system, open a terminal (or command prompt on Windows) and run...

`java -version`

...if the result is something like...

`java version "1.8.0_$BUILD_NUMBER`

...then you are good to go and can proceed to installing Scala and SBT. Note that the `$BUILD_NUMBER` will depend on the exact build of Java 8 you have installed.  If you are running older versions of the JDK (or a JDK is not installed on your machine), then you can down install the Java 8 JDK from either [Oracle](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) or the [OpenJDK 8 project](http://openjdk.java.net/projects/jdk8/).

#### Oracle JDK 8
Pre-packaged installers for Oracle's JDK 8 are [available](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for all major operating systems. If you are new to Java development (and/or are *not* using a Linux-based OS!) then I would suggest that you use one of Oracle's pre-packaged installers.

#### OpenJDK 8
The OpenJDK 8 project is an open-source reference implementation of the Oracle Java SE 8 Platform Specification. Installing the OpenJDK on Linux systems is a [piece of cake](http://openjdk.java.net/install/).  For example, Debian or Ubuntu users just need to open a terminal and run...

`sudo apt-get install openjdk-8-jdk`

...installing OpenJDK on Mac OSX can be done but requires a bit more work.  While I am sure it is possible to install the OpenJDK on Windows, I don't have any idea how to go about doing it!

#### Testing your Java install
To verify your Java install, open a terminal (or command prompt on Windows) and run...

`java -version`

...and the result should be something like...

`java version "1.8.0_$BUILD_NUMBER`

...where the `$BUILD_NUMBER` will depend on the exact build of Java 8 you have installed

### Scala and SBT
Once Java 8 is installed, need to install Scala and SBT. Installers exist for all major operating systems for both [Scala](http://www.scala-lang.org/download/) and [SBT](http://www.scala-sbt.org/download.html).

An alternative solution is to install [Activator](https://www.lightbend.com/activator/download) from LightBend which includes both Scala and SBT (as well as the [Play Framework](https://www.playframework.com/)).

#### Testing your Scala and SBT install
To verify your Scala install, open a terminal (or command prompt on Windows) and run...

`scala -version`

...and the result should be something like...

`Scala code runner version 2.11.8 -- Copyright 2002-2016, LAMP/EPFL`

To verify your SBT install, open a terminal (or command prompt on Windows) and run...

`sbt sbtVersion`

...and the result should be something like...

```
[info] Loading global plugins from C:\Users\pughdr\.sbt\0.13\plugins
[info] Loading project definition from C:\Users\pughdr\Research\scalabm\markets-sandbox\project
[info] Set current project to markets-sandbox (in build file:/C:/Users/pughdr/Research/scalabm/markets-sandbox/)
[info] 0.13.11
```
