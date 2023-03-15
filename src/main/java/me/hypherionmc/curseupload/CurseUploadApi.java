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
package me.hypherionmc.curseupload;

import me.hypherionmc.curseupload.requests.CurseArtifact;
import me.hypherionmc.curseupload.requests.GameVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * @author HypherionSA
 * Main CurseUpload4J Interface
 */
public class CurseUploadApi {

    // A static reference to an instance of this class. Used internally
    public static CurseUploadApi INSTANCE;
    private final Logger logger;

    // Upload API Token. Required
    private final String apiKey;

    // Reference to the GameVersions API request
    private final GameVersions gameVersions;

    // Test the API without actually uploading anything
    private boolean debug = false;

    /**
     * Create a new API Client
     * @param apiKey API Key REQUIRED to use any of the upload endpoints
     */
    public CurseUploadApi(String apiKey) {
        this(apiKey, LoggerFactory.getLogger("CurseUpload4J"));
    }

    /**
     * Create a new API Client
     * @param apiKey API Key REQUIRED to use any of the upload endpoints
     * @param logger SLF4J Logger to use instead of the default one
     */
    public CurseUploadApi(String apiKey, Logger logger) {
        this.apiKey = apiKey;
        this.logger = logger;
        this.gameVersions = new GameVersions();
        INSTANCE = this;

        this.gameVersions.refresh();
    }

    /**
     * Enable Debug Mode.
     * When enabled, no files will actually be uploaded
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public GameVersions getGameVersions() {
        return gameVersions;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * Used to upload a {@link CurseArtifact} and it's children. Use this instead of
     * calling upload on the {@link CurseArtifact}
     */
    public void upload(CurseArtifact artifact) throws FileNotFoundException {
        artifact.upload();

        artifact.getChildren().forEach(a -> {
            try {
                a.upload();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Logger getLogger() {
        return logger;
    }
}
