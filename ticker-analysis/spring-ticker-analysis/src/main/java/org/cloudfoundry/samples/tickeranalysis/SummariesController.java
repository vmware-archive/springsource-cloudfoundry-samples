/*
 * Copyright (c) 2011 by the original author(s).
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

package org.cloudfoundry.samples.tickeranalysis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
@Controller
@RequestMapping("/summaries")
public class SummariesController {

	@Autowired
	private SummaryService summaryService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody List<Summary> summaries() {
		return summaryService.getSummaries();
	}

	@RequestMapping(value = "/{symbol}", method = RequestMethod.GET)
	public @ResponseBody Summary summary(@PathVariable String symbol) {
		return summaryService.getSummary(symbol);
	}
}
