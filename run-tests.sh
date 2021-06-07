#!/usr/bin/env sh
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -ev

./gradlew publishToMavenLocal

./gradlew -b examples/test1/build.gradle --refresh-dependencies --rerun-tasks clean check

./gradlew -b examples/test2/build.gradle --refresh-dependencies --rerun-tasks clean check

# build example again to make sure the download in the previous run works
./gradlew -b examples/test2/build.gradle --refresh-dependencies --rerun-tasks check
