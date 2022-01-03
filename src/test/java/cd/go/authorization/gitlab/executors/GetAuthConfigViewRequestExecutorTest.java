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

package cd.go.authorization.gitlab.executors;

import cd.go.authorization.gitlab.annotation.MetadataHelper;
import cd.go.authorization.gitlab.annotation.ProfileMetadata;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.utils.Util;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAuthConfigViewRequestExecutorTest {

    @Test
    public void shouldRenderTheTemplateInJSON() throws Exception {
        GoPluginApiResponse response = new GetAuthConfigViewRequestExecutor().execute();
        assertThat(response.responseCode()).isEqualTo(200);
        Map<String, String> hashSet = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertThat(hashSet).containsEntry("template", Util.readResource("/auth-config.template.html"));
    }

    @Test
    public void allFieldsShouldBePresentInView() throws Exception {
        String template = Util.readResource("/auth-config.template.html");

        for (ProfileMetadata field : MetadataHelper.getMetadata(GitLabConfiguration.class)) {
            assertThat(template).contains("ng-model=\"" + field.getKey() + "\"");
            assertThat(template).contains("<span class=\"form_error form-error\" ng-class=\"{'is-visible': GOINPUTNAME[" +
                    field.getKey() + "].$error.server}\" ng-show=\"GOINPUTNAME[" +
                    field.getKey() + "].$error.server\">{{GOINPUTNAME[" +
                    field.getKey() + "].$error.server}}</span>");
        }
    }
}