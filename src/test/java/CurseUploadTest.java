import me.hypherionmc.curseupload.CurseUploadApi;
import me.hypherionmc.curseupload.constants.CurseChangelogType;
import me.hypherionmc.curseupload.constants.CurseReleaseType;
import me.hypherionmc.curseupload.requests.CurseArtifact;

import java.io.File;

public class CurseUploadTest {

    public static void main(String[] args) {
        CurseUploadApi uploadApi = new CurseUploadApi("INVALID");
        uploadApi.setDebug(true);

        CurseArtifact artifact = new CurseArtifact(new File("dummy.jar"), 12345L);
        artifact.changelog("This is a sample changelog");
        artifact.changelogType(CurseChangelogType.TEXT);
        artifact.addGameVersion("1.19.2").addGameVersion("1.16.5");
        artifact.addGameVersion("client").addGameVersion("server");
        artifact.modLoader("forge").modLoader("fabric");
        artifact.displayName("This is a sample file");
        artifact.releaseType(CurseReleaseType.BETA);
        artifact.requirement("fabric-api");

        try {
            uploadApi.upload(artifact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
