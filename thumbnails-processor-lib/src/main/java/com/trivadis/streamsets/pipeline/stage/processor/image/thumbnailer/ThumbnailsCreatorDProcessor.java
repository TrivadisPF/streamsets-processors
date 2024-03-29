/*
 * Copyright 2017 StreamSets Inc.
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
package com.trivadis.streamsets.pipeline.stage.processor.image.thumbnailer;

import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.api.base.configurablestage.DProcessor;
import com.trivadis.streamsets.pipeline.stage.processor.image.thumbnailer.config.JobConfig;

@StageDef(
		  version = 1,
		  label = "Thumbnails Creator",
		  description = "Use the Thumbnailator Library to create thumbnails from images",
		  execution = { ExecutionMode.STANDALONE },
		  icon = "thumbnails_processor.png",
		  onlineHelpRefUrl =""
		)
@ConfigGroups(Groups.class)
@GenerateResourceBundle
public class ThumbnailsCreatorDProcessor extends DProcessor {
	@ConfigDefBean
	public JobConfig jobConfig;

	@Override
	protected Processor createProcessor() {
		return new ThumbnailsCreatorProcessor(jobConfig);
	}
}

