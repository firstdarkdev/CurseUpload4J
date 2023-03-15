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

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.hypherionmc.curseupload.CurseUploadApi;
import me.hypherionmc.curseupload.schema.versions.Version;
import me.hypherionmc.curseupload.schema.versions.VersionType;
import me.hypherionmc.curseupload.util.HTTPUtils;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import static me.hypherionmc.curseupload.constants.ApiEndpoints.VERSION_TYPES_URL;
import static me.hypherionmc.curseupload.constants.ApiEndpoints.VERSION_URL;

/**
 * @author HypherionSA
 * Used to fetch and validate supported Minecraft Versions from Curseforge
 */
public class GameVersions {

    // Cached Versions
    private final TObjectLongMap<String> gameVersions = new TObjectLongHashMap<>();

    // Update or load the cache
    public void refresh() {
        if (CurseUploadApi.INSTANCE == null) {
            throw new NullPointerException("CurseUploadAPI is null. Did you forget to initialize it?");
        }
        this.fetchValidVersionTypes();
    }

    /**
     * Get supported game versions from Curseforge and filter out the correct values
     */
    private void fetchValidVersionTypes() {
        this.gameVersions.clear();

        try {
            TLongSet validVersionTypes = new TLongHashSet();

            Reader versionReader = HTTPUtils.fetch(VERSION_TYPES_URL, CurseUploadApi.INSTANCE.getApiKey());
            VersionType[] types = HTTPUtils.gson.fromJson(versionReader, VersionType[].class);
            versionReader.close();

            for (VersionType type : types) {
                if (type.slug().startsWith("minecraft") || type.slug().equals("java") || type.slug().equals("modloader")) {
                    validVersionTypes.add(type.id());
                }
            }

            Reader gameVersionJson = HTTPUtils.fetch(VERSION_URL, CurseUploadApi.INSTANCE.getApiKey());
            Version[] versions = HTTPUtils.gson.fromJson(gameVersionJson, Version[].class);
            gameVersionJson.close();

            for (Version version : versions) {
                if (validVersionTypes.contains(version.gameVersionTypeID())) {
                    gameVersions.put(version.name().toLowerCase(), version.id());
                }
            }
        } catch (Exception e) {
            CurseUploadApi.INSTANCE.getLogger().error("Failed to fetch Curseforge Versions", e);
        }
    }

    /**
     * Convert a String list of Game Versions into their ID counterparts
     * Used when sending a request to the API
     * @param objects The list of game versions to check
     * @return The list of game versions ID's if no error occurred
     */
    public Set<Long> resolveGameVersion(Set<String> objects) {
        Set<Long> set = new HashSet<>();

        objects.forEach(obj -> {
            long id = gameVersions.get(obj.toLowerCase());
            if (id == 0) {
                throw new IllegalArgumentException(obj + " is not a valid game version. Valid versions are: " + gameVersions.keySet());
            }
            set.add(id);
        });

        return set;
    }
}
