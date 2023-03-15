/*
 * This file is part of CurseUpload4J, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 HypherionSA and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.hypherionmc.curseupload.requests;

import com.google.gson.JsonObject;
import me.hypherionmc.curseupload.CurseUploadApi;
import me.hypherionmc.curseupload.constants.CurseChangelogType;
import me.hypherionmc.curseupload.constants.CurseRelationType;
import me.hypherionmc.curseupload.constants.CurseReleaseType;
import me.hypherionmc.curseupload.schema.meta.CurseMetaData;
import me.hypherionmc.curseupload.schema.meta.ProjectRelations;
import me.hypherionmc.curseupload.schema.responses.ResponseError;
import me.hypherionmc.curseupload.schema.responses.ResponseSuccess;
import me.hypherionmc.curseupload.util.HTTPUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;

import static me.hypherionmc.curseupload.constants.ApiEndpoints.UPLOAD_URL;

/**
 * @author HypherionSA
 * POJO and Helper class for managing the actual upload
 */
public class CurseArtifact {

    // Internal Use Only
    private transient final long projectId;
    private transient final CurseArtifact parent;
    private transient final File artifact;
    private transient long curseFileId;
    private transient Set<Long> uploadVersions;
    private transient final List<CurseArtifact> children = new ArrayList<>();
    private transient Map<String, String> relationships = new HashMap<>();
    private transient final ProjectRelations uploadRelations = new ProjectRelations();

    // Can be changed
    private String changelog;
    private CurseChangelogType changelogType = CurseChangelogType.TEXT;
    private String displayName = null;
    private final Set<String> gameVersions = new HashSet<>();
    private CurseReleaseType releaseType = CurseReleaseType.RELEASE;

    /**
     * Create a new instance of a CurseArtifact to be uploaded
     * @param artifact The file that will be uploaded
     * @param projectId The Project ID of the curse project where the file will be uploaded
     */
    public CurseArtifact(File artifact, Long projectId) {
        this(artifact, projectId, null);
    }

    protected CurseArtifact(File artifact, Long projectId, CurseArtifact parent) {
        this.artifact = artifact;
        this.projectId = projectId;
        this.parent = parent;
    }

    /**
     * Set a Changelog that will be added to the file on upload
     */
    public CurseArtifact changelog(String changelog) {
        this.changelog = changelog;
        return this;
    }

    /**
     * The FORMAT of the changelog. See {@link CurseChangelogType}
     */
    public CurseArtifact changelogType(CurseChangelogType type) {
        this.changelogType = type;
        return this;
    }

    /**
     * Optional Display Name that will be added to the uploaded file
     */
    public CurseArtifact displayName(String name) {
        this.displayName = displayName;
        return this;
    }

    /**
     * The Type of Release of the uploaded file. See {@link CurseReleaseType}
     */
    public CurseArtifact releaseType(CurseReleaseType type) {
        this.releaseType = type;
        return this;
    }

    /**
     * Add a file that will be uploaded along with the main file
     * @param file The file to be uploaded
     */
    public CurseArtifact addAdditionalFile(File file) {
        if (this.parent != null) {
            throw new IllegalArgumentException("Child artifacts must nog have their own children.");
        }

        final CurseArtifact child = new CurseArtifact(file, this.projectId, this);
        child.changelogType = this.changelogType;
        child.changelog = this.changelog;
        child.releaseType = this.releaseType;
        child.relationships = new HashMap<>(this.relationships);

        this.children.add(child);
        return child;
    }

    /**
     * Add an incompatible dependency
     */
    public CurseArtifact incompatibility(String slug) {
        return this.addRelation(CurseRelationType.INCOMPATIBLE, slug);
    }

    /**
     * Add a required dependency
     */
    public CurseArtifact requirement(String slug) {
        return this.addRelation(CurseRelationType.REQUIRED, slug);
    }

    /**
     * Add a dependency that is embedded in the mod
     */
    public CurseArtifact embedded(String slug) {
        return this.addRelation(CurseRelationType.EMBEDDED, slug);
    }

    /**
     * Add a TOOL type dependency
     */
    public CurseArtifact tool(String slug) {
        return this.addRelation(CurseRelationType.TOOL, slug);
    }

    /**
     * Add an optional Dependency
     */
    public CurseArtifact optional(String slug) {
        return this.addRelation(CurseRelationType.OPTIONAL, slug);
    }

    /**
     * Add a supported modloader
     */
    public CurseArtifact modLoader(String modloader) {
        return addGameVersion(modloader);
    }

    /**
     * Add a supported JAVA version
     */
    public CurseArtifact javaVersion(String javaVersion) {
        return addGameVersion(javaVersion);
    }

    /**
     * Add any additional game versions that the mod supports
     */
    public CurseArtifact addGameVersion(String gameVersion) {
        if (this.parent != null) {
            throw new IllegalArgumentException("Sub files can not have their own versions!");
        }

        this.gameVersions.add(gameVersion);
        return this;
    }

    /**
     * INTERNAL
     */
    private CurseArtifact addRelation(CurseRelationType relationType, String slug) {
        final String existingRelation = relationships.get(slug);

        if (existingRelation != null) {
            this.relationships.remove(slug);
        }

        this.relationships.put(slug, relationType.toString());
        return this;
    }

    /**
     * Create the required MetaData that will be sent to the API
     */
    private CurseMetaData writeMetaData() {
        final CurseMetaData metaData = new CurseMetaData();
        metaData.changelog = (changelog == null || changelog.isEmpty()) ? "Coming Soon!" : changelog;
        metaData.changelogType = changelogType;

        if (displayName != null) {
            metaData.displayName = displayName;
        }

        metaData.releaseType = releaseType;

        if (!this.uploadRelations.projects.isEmpty()) {
            metaData.relations = this.uploadRelations;
        }

        if (this.parent == null) {
            metaData.gameVersions = this.uploadVersions;
        }

        if (this.parent != null) {
            metaData.gameVersions = null;
            metaData.parentFileID = this.parent.curseFileId;
        }

        return metaData;
    }

    /**
     * Check that all required info is supplied before trying to upload
     */
    private void validate() {
        CurseUploadApi.INSTANCE.getGameVersions().refresh();
        if (changelog == null || changelog.isEmpty()) {
            throw new IllegalArgumentException("Changelog cannot be empty");
        }

        if (gameVersions.isEmpty()) {
            throw new IllegalArgumentException("At-least 1 game version must be defined");
        }
    }

    /**
     * INTERNAL! DO NOT CALL DIRECTLY
     */
    @Deprecated
    public final void upload() throws FileNotFoundException {
        validate();
        prepareUpload();
        uploadArtifact();
    }

    /**
     * Process children and dependencies before uploading
     */
    private void prepareUpload() throws FileNotFoundException {
        if (!artifact.exists()) {
            throw new FileNotFoundException("Failed to find upload artifact");
        }

        for (Map.Entry<String, String> relation : this.relationships.entrySet()) {
            this.uploadRelations.addRelation(relation.getKey(), CurseRelationType.valueOf(relation.getValue()));
        }

        this.uploadVersions = CurseUploadApi.INSTANCE.getGameVersions().resolveGameVersion(gameVersions);
    }

    /**
     * Actually upload the damn file
     */
    private void uploadArtifact() {
        final HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).setUserAgent("CurseUpload4J").build();

        final MultipartEntityBuilder requestBody = MultipartEntityBuilder.create();
        requestBody.addTextBody("metadata", HTTPUtils.gson.toJson(this.writeMetaData()), ContentType.APPLICATION_JSON);
        requestBody.addBinaryBody("file", this.artifact);

        final HttpPost request = new HttpPost(String.format(UPLOAD_URL, this.projectId));
        request.addHeader("X-Api-Token", CurseUploadApi.INSTANCE.getApiKey());
        request.setEntity(requestBody.build());

        if (!CurseUploadApi.INSTANCE.isDebug()) {
            try {
                final HttpResponse response = client.execute(request);

                if (response.getStatusLine().getStatusCode() == 200) {
                    final InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
                    this.curseFileId = HTTPUtils.gson.fromJson(reader, ResponseSuccess.class).id;
                    reader.close();
                    CurseUploadApi.INSTANCE.getLogger().log(Level.INFO, String.format("Successfully uploaded artifact %s with ID %s", this.artifact.getName(), this.curseFileId));
                } else {
                    int errorCode = response.getStatusLine().getStatusCode();
                    String errorMessage = response.getStatusLine().getReasonPhrase();

                    if (response.getFirstHeader("content-type").getValue().contains("json")) {
                        final InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
                        ResponseError error = HTTPUtils.gson.fromJson(reader, ResponseError.class);
                        reader.close();

                        errorCode = error.errorCode;
                        errorMessage = error.errorMessage;
                    }
                    CurseUploadApi.INSTANCE.getLogger().log(Level.SEVERE, String.format("Failed to Upload artifact to Curseforge. Code: %s, Error: %s", errorCode, errorMessage));
                    System.err.println(errorCode + ": " + errorMessage);
                }
            } catch (Exception e) {
                CurseUploadApi.INSTANCE.getLogger().log(Level.SEVERE, "Failed to Upload artifact to Curseforge.", e);
            }
        } else {
            // Do not upload the file. Instead, write the JSON that will be sent to the console
            JsonObject object = new JsonObject();
            object.addProperty("metadata", HTTPUtils.gson.toJson(this.writeMetaData()));
            object.addProperty("file", this.artifact.getName());

            CurseUploadApi.INSTANCE.getLogger().log(Level.INFO, HTTPUtils.gson.toJson(object));
        }
    }

    public List<CurseArtifact> getChildren() {
        return children;
    }

    public long getCurseFileId() {
        return curseFileId;
    }
}
