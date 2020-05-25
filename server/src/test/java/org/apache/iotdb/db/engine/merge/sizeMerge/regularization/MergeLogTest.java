/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.engine.merge.sizeMerge.regularization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.iotdb.db.engine.merge.MergeTest;
import org.apache.iotdb.db.engine.merge.manage.MergeResource;
import org.apache.iotdb.db.engine.merge.sizeMerge.regularization.task.RegularizationMergeTask;
import org.apache.iotdb.db.engine.storagegroup.TsFileResource;
import org.apache.iotdb.db.exception.StorageEngineException;
import org.apache.iotdb.db.exception.metadata.MetadataException;
import org.apache.iotdb.tsfile.exception.write.WriteProcessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MergeLogTest extends MergeTest {

  private File tempSGDir;

  @Before
  public void setUp() throws IOException, WriteProcessException, MetadataException {
    super.setUp();
    tempSGDir = new File("tempSG");
    tempSGDir.mkdirs();
  }

  @After
  public void tearDown() throws IOException, StorageEngineException {
    super.tearDown();
    FileUtils.deleteDirectory(tempSGDir);
  }

  @Test
  public void testMergeLog() throws Exception {
    RegularizationMergeTask mergeTask =
        new RegularizationMergeTask(
            new MergeResource(seqResources),
            tempSGDir.getPath(), this::testCallBack, "test", MERGE_TEST_SG);
    mergeTask.call();
  }

  private void testCallBack(List<TsFileResource> seqFiles, List<TsFileResource> unseqFiles,
      File mergeLog, List<TsFileResource> newFile) {
    int lineCnt = 0;
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(mergeLog))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        lineCnt++;
        System.out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    assertEquals(3, lineCnt);
    try {
      for (TsFileResource fileResource : newFile) {
        fileResource.close();
        fileResource.remove();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
