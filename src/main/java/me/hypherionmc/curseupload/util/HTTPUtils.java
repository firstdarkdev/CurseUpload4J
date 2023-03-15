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
package me.hypherionmc.curseupload.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * @author HypherionSA
 * HTTP Requests helper class
 */
public class HTTPUtils {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Make a GET request to the Curseforge API
     * @param url The API Endpoint that will be called
     * @param token The API Token. Required
     */
    public static Reader fetch(String url, String token) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", "CurseUpload4J");
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.addRequestProperty("X-Api-Token", token);

        if (connection.getContentEncoding().equals("gzip")) {
            return new InputStreamReader(new GZIPInputStream(connection.getInputStream()));
        }

        return new InputStreamReader(connection.getInputStream());
    }

}
