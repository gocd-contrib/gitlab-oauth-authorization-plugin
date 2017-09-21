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

package cd.go.authorization.gitlab.client.models;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MembershipInfoTest {
    @Test
    public void shouldDeserializeJSON() throws Exception {
        final MembershipInfo membershipInfo = MembershipInfo.fromJSON("{\n" +
                "  \"id\": 1,\n" +
                "  \"username\": \"raymond_smith\",\n" +
                "  \"name\": \"Raymond Smith\",\n" +
                "  \"state\": \"active\",\n" +
                "  \"created_at\": \"2012-10-22T14:13:35Z\",\n" +
                "  \"access_level\": 30" +
                "}\n");

        assertThat(membershipInfo.getId(), is(1L));
        assertThat(membershipInfo.getUsername(), is("raymond_smith"));
        assertThat(membershipInfo.getName(), is("Raymond Smith"));
        assertThat(membershipInfo.getState(), is("active"));
        assertThat(membershipInfo.getCreatedAt(), is("2012-10-22T14:13:35Z"));
        assertThat(membershipInfo.getAccessLevel(), is(AccessLevel.DEVELOPER));
    }
}