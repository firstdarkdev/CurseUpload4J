package me.hypherionmc.curseupload.constants;

public enum GameType {
    MINECRAFT(432, "https://minecraft.curseforge.com/api"),
    HYTALE(70216, "https://legacy.curseforge.com/api");

    private final long gameId;
    private final String baseApiUrl;

    GameType(long id, String apiUrl) {
        this.gameId = id;
        this.baseApiUrl = apiUrl;
    }

    public long getGameId() {
        return this.gameId;
    }

    public String uploadEndpoint() {
        return this.baseApiUrl + "/projects/%s/upload-file";
    }

    /// The normal CF API doesn't include the data for Hytale.
    /// For now, we use a proxy to return the data from the CurseForge Studios API.
    /// This API requires a different API key that authors won't normally possess and as such won't be able to use
    /// These endpoints just return the raw data from the CF api without exposing our API key

    public String versionTypesEndpoint() {
        return String.format("https://cfdata.firstdark.dev/%s/version-types", this.gameId);
    }

    public String versionsEndpoint() {
        return String.format("https://cfdata.firstdark.dev/%s/versions", this.gameId);
    }
}
