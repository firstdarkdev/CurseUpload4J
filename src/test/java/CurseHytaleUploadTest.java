import me.hypherionmc.curseupload.CurseUploadApi;
import me.hypherionmc.curseupload.constants.CurseChangelogType;
import me.hypherionmc.curseupload.constants.CurseReleaseType;
import me.hypherionmc.curseupload.constants.GameType;
import me.hypherionmc.curseupload.requests.CurseArtifact;

import java.io.File;

public class CurseHytaleUploadTest {

    public static void main(String[] args) {
        CurseUploadApi uploadApi = new CurseUploadApi("INVALID");
        uploadApi.setGameType(GameType.HYTALE);
        uploadApi.setDebug(true);

        CurseArtifact artifact = new CurseArtifact(new File("dummy.jar"), 12345L);
        artifact.changelog("This is a sample changelog");
        artifact.changelogType(CurseChangelogType.TEXT);
        artifact.addGameVersion("Early Access");
        artifact.displayName("This is a sample file");
        artifact.releaseType(CurseReleaseType.BETA);

        try {
            uploadApi.upload(artifact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
