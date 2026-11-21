# Releasing to Maven Central

`mvn -Prelease clean deploy` signs the parent POM and each module's jar,
sources jar, javadoc jar and POM, then uploads all four artifacts
(`tui-java`, `tui-commons`, `tui-mail`, `tui-web`) to the
[Central Portal](https://central.sonatype.com) as one deployment. The upload
stops at **validated**. Nothing is public until you press **Publish** in the
portal, and a published version can never be deleted or replaced.

## One-time setup

These are shared with faux-person, so if you've released that, they're done.

1. **Namespace.** `org.tauasa` is verified on the Central Portal by a DNS TXT
   record on `tauasa.org`. Keep the record.
2. **A user token.** In the portal, open *View Account*, then *Generate User
   Token*. `~/.m2/settings.xml` reads it from two environment variables, so
   the token is never stored in a file, and never in this repo:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>${env.MAVEN_CENTRAL_USERNAME}</username>
         <password>${env.MAVEN_CENTRAL_PASSWORD}</password>
       </server>
     </servers>
   </settings>
   ```

3. **A GPG key that Central can check**, sent to `keyserver.ubuntu.com`.
   Signing asks for its passphrase; on macOS set `export GPG_TTY=$(tty)` first,
   or gpg fails with exit code 2.

## Each release

```bash
git switch main && git pull
export GPG_TTY=$(tty)   # and MAVEN_CENTRAL_USERNAME / MAVEN_CENTRAL_PASSWORD
# <version> must not end in -SNAPSHOT: the release profile refuses it
mvn -Prelease clean deploy
```

To check the build without signing or uploading:

```bash
mvn -Prelease -Dgpg.skip clean verify
```

Then go to *Deployments* in the portal. Check that it lists all four
artifacts at the same version, and press **Publish**, or **Drop** to discard
the upload. After publishing, tag the commit you built from:

```bash
git tag -a v1.0.1 -m "tui-java 1.0.1" && git push origin v1.0.1
```

Then set `<version>` to the next `-SNAPSHOT` in the parent POM and in each
module's `<parent>` block. A version can take up to 30 minutes to appear on
`repo1.maven.org`, and longer to show up in search.
