
# **tui-java**

This is a toolbox of java stuffs I started putting together in 1998.

It evolved as java evolved beginning with `JDK1.1`. Active development ended around 2012 but I recently started using it again for prototyping. 

Most of the functionality in this library is available via Spring, Guice, Apache Commons, and/or later JDK releases. I still use it out of habit and because it's easy.

A lot of this project is `code archeology`. Its unit tests cover the security fixes in 1.0.1, not the whole library. It's still used in a few legacy/enterprise production environments but use at your own risk.

I've gotten the most mileage out of these classes 
| Class | Module      |
|-------|-------------|
| [Utils](./tui-commons/src/main/java/org/tauasa/commons/util/Utils.java) | tui-commons |
| [DateUtils](./tui-commons/src/main/java/org/tauasa/commons/util/DateUtils.java) | tui-commons|
| [IOUtils](./tui-commons/src/main/java/org/tauasa/commons/io/IOUtils.java) | tui-commons |
| [XFile](./tui-commons/src/main/java/org/tauasa/commons/io/XFile.java) | tui-commons |
| [ServletHelper](./tui-web/src/main/java/org/tauasa/web/ServletHelper.java) | tui-web |

# Use it

Each module is a separate artifact under `org.tauasa` on
[Maven Central](https://central.sonatype.com/namespace/org.tauasa), built for
Java 17 or later. `tui-mail` brings in `tui-commons`, and `tui-web` brings in both:

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
`javax.mail-api`, `javax.activation-api` and `javax.servlet-api`.

Two classes need a library you add yourself:

| Class | Add |
|---|---|
| `SpringApplicationContext` | `org.springframework:spring-context` (any Spring your app already uses) |
| `StringDistanceUtils` | `com.wcohen:com.wcohen.secondstring:0.1`, which brings log4j 1.2.17 with known vulnerabilities |

## Changes in 1.0.2 (not yet released)

- **commons-lang is gone**, which also removes commons-lang 2.1's known
  uncontrolled-recursion vulnerability. If your code used commons-lang through
  this library, add it to your own POM.
- **HTML escaping is JDK code**: `Utils.escapeHtml(String)` replaces
  `StringEscapeUtils.escapeHtml` in `HTMLDataModelWriter` and the smoke-test
  renderers. It now escapes the single quote too, so escaped text is safe in an
  attribute quoted with either quote. Characters outside printable ASCII come
  out as numeric references (`&#233;` for `é`) in place of named ones
  (`&eacute;`): they display the same, and they're valid XHTML.
- **`toString()` uses `Utils.toStringOf`** in `GeohashBean`, `Host` and
  `RequestBean`, in the same `Type@hash[name=value,...]` form as before.

## Changes in 1.0.1

Several of these change behavior. Read them before upgrading.

- **`SmoketestServlet` requires a role.** Set `smoketest.required.role` in
  `META-INF/smoketest.properties` to a container role. Without it, or for a user
  without that role, every request gets 403. Tests are no longer verbose unless a
  test sets `verbose=true`, and the `_res` download parameter is gone. The
  `smoketest.allow.virtual.host` check reads the client's `Host` header, so it
  isn't access control.
- **`JQueryXHTMLRenderer` is gone**, with the jQuery 1.3.2 and jQuery UI 1.7.2
  files it served, which have known XSS vulnerabilities. If your
  `smoketest.properties` names it, `SmoketestServlet` stops at startup and says
  to use `org.tauasa.web.smoke.XHTMLRenderer` instead.
- **`HTTPUtils` uses the JDK's `java.net.http.HttpClient`** in place of
  commons-httpclient 3.1. HTTPS certificates and host names are always checked,
  every request times out after 30 seconds unless you set another timeout, and
  Basic credentials are refused over plain `http://` and never follow a redirect.
  POSTs don't follow redirects at all, so headers you add can't reach another host.
  `createHttpClient` now returns a `java.net.http.HttpClient`. The geocoders
  default to HTTPS.
- **`SMTPMailService` has no default server.** Call `setHost` or set
  `mail.smtp.host`; it no longer falls back to `smtp.tauasa.org`. It doesn't
  change `System` properties any more, and each service keeps its own session.
- **Mail subjects, headers, recipient names and the from address with a line
  break are refused**, so user input can't add a `Bcc` or other headers.
  `new EmailMessage(template, to)` copies the template's recipients, headers and
  attachments, so messages made from one template don't collect each other's
  recipients.
- **`XmlUtils.DOCUMENT_BUILDER_FACTORY` is private**, so no code can switch off
  its DOCTYPE and external-entity protection.
- **`XPathUtils.evaluate(InputStream|InputSource, ...)`** parses with the
  hardened parser that refuses DOCTYPEs and external entities, and now declares
  the parser's exceptions.
- **Role checks with an empty role list return false** (`HttpServletRequestHelper`,
  `JSTL`), so a missing setting doesn't let everyone in.
- **HTML output is escaped**: the smoke-test formatters and `HTMLDataModelWriter`.
  `LogoutFilter` URL-encodes the parameters it copies into its redirect.
- **`ThreadLocalDAO` keeps one connection per DAO**, not one shared by every DAO on
  the thread. Call `closeAll()` in a `finally` block.
- No password, init-param value, SQL bind value or `smoketest.properties`
  value is written to the logs.
- Dependencies: Guava, Guice, opencsv, statsd and commons-httpclient are gone.
  Spring and secondstring are optional. The modules depend on each other at the
  same version, and every version is pinned.

# Build

Needs JDK 17 or later. This builds all three modules and runs the tests:

```bash
mvn clean install
```

GitHub Actions runs the same build on every push and pull request.

[RELEASING.md](RELEASING.md) has how a version goes to Maven Central. MIT
licensed: see [LICENSE](LICENSE).



