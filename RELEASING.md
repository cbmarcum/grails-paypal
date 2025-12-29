# Releasing

## Creating a Release
Releases are created in the GitHub repo by creating a tag and a Release based on that tag and a Changelog of major changes and uploading and source archives.  Build artifacts are uploaded to Maven Central.

This is automated by [JReleaser](https://jreleaser.org/) using the `jreleaser.yml` configuration file and the [JReleaser CLI](https://jreleaser.org/guide/latest/tools/jreleaser-cli.html).

### Pre-Release Checklist
1. App version is correct in `build.gradle` and `jreleaser.yml` files.
2. Test with `./gradlew clean check` and make sure all tests pass.
3. Build with `./gradlew publishMyLibraryPublicationToMyRepository`.

### Steps to Create a Release
1. `export JRELEASER_OUTPUT_DIRECTORY=build`
2. `jreleaser config`
3. `jreleaser full-release --dry-run`
4. Check `build/jreleaser/release/CHANGELOG.md` for errors. (Correct on GitHub after release)
5. `jreleaser full-release`
6. Check [GitHub Releases](https://github.com/cbmarcum/grails-paypal/releases) and [Maven Central](https://central.sonatype.com) for release.