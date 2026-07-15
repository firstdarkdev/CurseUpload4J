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
package me.hypherionmc.curseupload.errors;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

/**
 * @author MattSturgeon
 * Thrown when using Minecraft Versions that are not supported by Curseforge
 */
public class InvalidCurseVersionException extends IllegalArgumentException {
    private final List<String> invalidVersions;
    private final List<String> validVersions;

    public static InvalidCurseVersionException of(Collection<String> invalidVersions, Collection<String> validVersions) {
        List<String> invalid = invalidVersions.stream().sorted().collect(Collectors.toList());
        List<String> valid = validVersions.stream().sorted().collect(Collectors.toList());
        return new InvalidCurseVersionException(invalid, valid);
    }

    private static String message(List<String> invalid, List<String> valid) {
        StringBuilder msg = new StringBuilder();
        switch (invalid.size()) {
            case 0: break;
            case 1:
                String v = invalid.stream().findFirst().orElse(null);
                msg.append(v).append(" is not a valid game version. ");
                break;
            default:
                msg.append("Invalid game versions: ");
                msg.append(String.join(", ", invalid));
                msg.append(". ");
                break;
        }
        msg.append("Valid versions are: ");
        msg.append(String.join(", ", valid));
        return msg.toString();
    }

    private InvalidCurseVersionException(List<String> invalidVersions, List<String> validVersions) {
        super(message(invalidVersions, validVersions));
        this.invalidVersions = unmodifiableList(invalidVersions);
        this.validVersions = unmodifiableList(validVersions);
    }

    public final List<String> getInvalidVersions() {
        return invalidVersions;
    }

    public final List<String> getValidVersions() {
        return validVersions;
    }
}
