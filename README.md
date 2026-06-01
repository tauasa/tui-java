
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

# Build
```bash
mvn clean install
```



