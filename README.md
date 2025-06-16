# XProject

Contains my libraries

## Dependencies

Releases of Exposed are available in the Maven Central repository. You can declare this repository in your build script as follows:

```kotlin
repositories {
    mavenCentral()
}
```

## Modules

```kotlin
dependencies {
    implementation("io.github.com.xffc.xproject:core:1.0")
    implementation("io.github.com.xffc.xproject:kraft:1.2")
}
```

1. [core](core/README.md)
2. [kraft](kraft/README.md)

## Publishing to Maven Central

Add to your `~/.gradle/gradle.properties` following:

```properties
signing.keyId=last 8 chars from key
signing.password=key passphrase
signing.secretKeyRingFile=path to secring.gpg (example: C:\Users\xffc\.gnupg\secring.gpg)

mavenCentralUsername=your sonatype user
mavenCentralPassword=your sonatype token
```

Run 
```bash
gradlew publish
```

## License

[MIT License](LICENSE)
