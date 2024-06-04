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
package me.hypherionmc.curseupload.schema.meta;

import me.hypherionmc.curseupload.constants.CurseChangelogType;
import me.hypherionmc.curseupload.constants.CurseReleaseType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author HypherionSA
 * A POJO object that represents the required MetaData that has to be sent
 * to the upload API when a file is uploaded
 */
public class CurseMetaData {

    public String changelog = null;
    public CurseChangelogType changelogType = CurseChangelogType.TEXT;
    public String displayName = null;
    public Long parentFileID = null;
    public Set<Long> gameVersions = new HashSet<>();
    public CurseReleaseType releaseType = CurseReleaseType.RELEASE;
    public ProjectRelations relations = null;
    public boolean isMarkedForManualRelease = false;

}
