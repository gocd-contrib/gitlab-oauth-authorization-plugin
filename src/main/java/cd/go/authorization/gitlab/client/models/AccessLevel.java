/*
 * Copyright 2022 Thoughtworks, Inc.
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

package cd.go.authorization.gitlab.client.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public enum AccessLevel {
    @Expose
    @SerializedName("10")
    GUEST(10),
    @Expose
    @SerializedName("20")
    REPORTER(20),
    @Expose
    @SerializedName("30")
    DEVELOPER(30),
    @Expose
    @SerializedName("40")
    MASTER(40),
    @Expose
    @SerializedName("50")
    OWNER(50);

    private final int value;

    AccessLevel(int value) {
        this.value = value;
    }
}
