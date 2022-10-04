/*
 * Copyright 2022 EngFlow Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.engflow.bazel.invocation.analyzer.dataproviders.remoteexecution;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.engflow.bazel.invocation.analyzer.bazelprofile.BazelProfile;
import com.engflow.bazel.invocation.analyzer.core.DuplicateProviderException;
import com.engflow.bazel.invocation.analyzer.dataproviders.DataProviderUnitTestBase;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class TotalQueuingDurationDataProviderTest extends DataProviderUnitTestBase {
  private TotalQueuingDurationDataProvider provider;

  @Before
  public void setupTest() throws DuplicateProviderException {
    provider = new TotalQueuingDurationDataProvider();
    provider.register(dataManager);
    super.dataProvider = provider;
  }

  @Test
  public void shouldReturnNonZeroQueuingDuration() throws Exception {
    // TODO: Generate a small json with queuing in both the critical path and actions that are not
    // part of the critical path.
    String profilePath = RUNFILES.rlocation(ROOT + "bazel-profile-with_queuing.json.gz");
    BazelProfile bazelProfile = BazelProfile.createFromPath(profilePath);
    when(dataManager.getDatum(BazelProfile.class)).thenReturn(bazelProfile);

    TotalQueuingDuration queuing = provider.getTotalQueuingDuration();
    verify(dataManager).registerProvider(provider);
    verify(dataManager).getDatum(BazelProfile.class);
    verifyNoMoreInteractions(dataManager);

    assertThat(queuing.getTotalQueuingDuration()).isGreaterThan(Duration.ZERO);
  }

  @Test
  public void shouldReturnZeroQueuingDuration() throws Exception {
    String profilePath = RUNFILES.rlocation(ROOT + "tiny.json.gz");
    BazelProfile bazelProfile = BazelProfile.createFromPath(profilePath);
    when(dataManager.getDatum(BazelProfile.class)).thenReturn(bazelProfile);

    TotalQueuingDuration queuing = provider.getTotalQueuingDuration();
    verify(dataManager).registerProvider(provider);
    verify(dataManager).getDatum(BazelProfile.class);
    verifyNoMoreInteractions(dataManager);

    assertThat(queuing.getTotalQueuingDuration()).isEqualTo(Duration.ZERO);
  }
}
