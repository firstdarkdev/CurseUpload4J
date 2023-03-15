## CurseUpload4J

This project is an unofficial JAVA wrapper around the [Curseforge Upload API](https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-upload-api).

It was designed for use with our custom Mod Publishing plugin, but anyone can use it.

### Maven Setup

To use this library in your own Project, add the following maven repository:

```gradle
maven {
    url "https://maven.firstdarkdev.xyz/releases"
}
```

Next, add the library as a dependency (Replace VERSION with the one above):

![badge](https://maven.firstdarkdev.xyz/api/badge/latest/releases/me/hypherionmc/modutils/CurseUpload4j?color=40c14a&name=CurseUpload4J)

```gradle
implementation "me.hypherionmc.modutils:CurseUpload4j:VERSION"
```

---

### Example Usage

Before anything, you need to create a new `CurseUploadApi` client:

```java
public CurseUploadApi uploadApi = new CurseUploadApi(apiKey);
```

`apiKey` is your CurseForge Upload API key, and is REQUIRED!

After creating the client, you can access it anywhere using

```java
CurseUploadApi.INSTANCE
```

See test/java for more examples
