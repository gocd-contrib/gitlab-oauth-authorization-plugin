/*
 * Copyright 2024 ThoughtWorks, Inc.
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

package cd.go.authorization.gitlab.client;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class CodeChallengeGeneratorTest {

    @Test
    public void shouldGenerate() {
        List<String> codeChallengeDetails = CodeChallengeGenerator.generate();

        assertThat(codeChallengeDetails).satisfies(args -> {
            assertThat(args).hasSize(2);
            assertThat(args.get(0)).matches("[A-Za-z0-9_-]{43}");
            assertThat(args.get(1)).matches("[A-Za-z0-9_-]{43}");
        });
    }
}
