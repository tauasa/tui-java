
# **tui-java**

This is a toolbox of java stuffs I started putting together in 1998.

It evolved as java evolved beginning with `JDK1.1`. Active development ended around 2012 but I recently started using it again for prototyping. 

Most of the functionality in this library is available via Spring, Guice, Apache Commons, and/or later JDK releases. I still use it out of habit and because it's easy.

A lot of this project is `code archeology` without unit tests. It's still used in a few legacy/enterprise production environments but use at your own risk.

I've gotten the most mileage out of these classes 
| Class | Module      |
|-------|-------------|
| [Utils](./tui-commons/src/main/java/org/tauasa/commons/util/Utils.java) | tui-commons |
| [DateUtils](./tui-commons/src/main/java/org/tauasa/commons/util/DateUtils.java) | tui-commons|
| [IOUtils](./tui-commons/src/main/java/org/tauasa/commons/io/IOUtils.java) | tui-commons |
| [XFile](./tui-commons/src/main/java/org/tauasa/commons/io/XFile.java) | tui-commons |
| [ServletHelper](./tui-web/src/main/java/org/tauasa/web/ServletHelper.java) | tui-web |

# Use it

Each module is a separate artifact under `org.tauasa`. `tui-mail` brings in
`tui-commons`, and `tui-web` brings in both:

```xml
<dependency>
  <groupId>org.tauasa</groupId>
  <artifactId>tui-commons</artifactId>   <!-- or tui-mail, tui-web -->
  <version>1.0.1</version>
</dependency>
```

The library logs through SLF4J and doesn't choose a logging backend for you:
add the SLF4J binding your app uses, such as `slf4j-simple` or `logback-classic`.
`tui-mail` and `tui-web` also expect your container to provide
`javax.mail-api` and `javax.servlet-api`.

Several dependencies are old, and some have known vulnerabilities (Spring 4.1,
Guava 17, commons-httpclient 3.1, and log4j 1.2.17 through secondstring).
1.0.1 doesn't upgrade them. Check them against your own requirements before
you depend on this.

## Changes in 1.0.1

- `JQueryXHTMLRenderer` is gone, with the jQuery 1.3.2 and jQuery UI 1.7.2
  files it served, which have known XSS vulnerabilities. If your
  `smoketest.properties` names it, `SmoketestServlet` stops at startup and says
  to use `org.tauasa.web.smoke.XHTMLRenderer` instead.
- The modules depend on each other at the same version, and no dependency
  uses an open version range.

# Build
```bash
mvn clean install
```

[RELEASING.md](RELEASING.md) has how a version goes to Maven Central. MIT
licensed: see [LICENSE](LICENSE).



