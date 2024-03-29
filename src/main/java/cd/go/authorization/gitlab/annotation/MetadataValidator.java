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

package cd.go.authorization.gitlab.annotation;

import java.util.*;

public class MetadataValidator {

    public ValidationResult validate(Validatable configuration) {
        final ValidationResult validationResult = new ValidationResult();
        Map<String, String> properties = configuration.toProperties();

        List<String> knownFields = new ArrayList<>();
        for (ProfileMetadata field : MetadataHelper.getMetadata(configuration.getClass())) {
            knownFields.add(field.getKey());
            validationResult.addError(field.validate(properties.get(field.getKey())));
        }


        Set<String> set = new HashSet<>(properties.keySet());
        set.removeAll(knownFields);

        for (String key : set) {
            validationResult.addError(key, "Is an unknown property");
        }
        return validationResult;
    }
}
