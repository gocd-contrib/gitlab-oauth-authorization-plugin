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

public class Statistics {

    @SerializedName("commit_count")
    @Expose
    private long commitCount;
    @SerializedName("storage_size")
    @Expose
    private long storageSize;
    @SerializedName("repository_size")
    @Expose
    private long repositorySize;
    @SerializedName("lfs_objects_size")
    @Expose
    private long lfsObjectsSize;
    @SerializedName("job_artifacts_size")
    @Expose
    private long jobArtifactsSize;

    Statistics() {
    }

    public long getCommitCount() {
        return commitCount;
    }

    public long getStorageSize() {
        return storageSize;
    }

    public long getRepositorySize() {
        return repositorySize;
    }

    public long getLfsObjectsSize() {
        return lfsObjectsSize;
    }

    public long getJobArtifactsSize() {
        return jobArtifactsSize;
    }
}
