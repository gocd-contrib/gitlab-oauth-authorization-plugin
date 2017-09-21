/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.gitlab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class TokenInfo {

    @Expose
    @SerializedName("access_token")
    private String accessToken;
    @Expose
    @SerializedName("token_type")
    private String tokenType;

    @Expose
    @SerializedName("expires_in")
    private int expiresIn;

    @Expose
    @SerializedName("refresh_token")
    private String refreshToken;

    public TokenInfo() {
    }

    public TokenInfo(String accessToken, String tokenType, int expiresIn, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public String accessToken() {
        return accessToken;
    }

    public String tokenType() {
        return tokenType;
    }


    public int expiresIn() {
        return expiresIn;
    }

    public String refreshToken() {
        return refreshToken;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static TokenInfo fromJSON(String json) {
        return GSON.fromJson(json, TokenInfo.class);
    }
}
