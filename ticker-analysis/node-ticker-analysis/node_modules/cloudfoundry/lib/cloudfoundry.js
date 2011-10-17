/*
	cloudfoundry helper library
	https://github.com/igo/cloudfoundry
	
	Copyright (c) 2011 by Igor Urmincek

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

exports.cloud = typeof process.env.VCAP_APPLICATION != 'undefined';

exports.host = process.env.VCAP_APP_HOST;
exports.port = process.env.VCAP_APP_PORT;

exports.app = function() {
	if (exports.cloud) {
		return JSON.parse(process.env.VCAP_APPLICATION);
	} else {
		return undefined;
	}
}();

exports.services = function() {
	if (exports.cloud) {
		return JSON.parse(process.env.VCAP_SERVICES);
	} else {
		return {};
	}
}();

exports.mongodb = {};
exports.redis = {};
exports.mysql = {};

var item, service, list, i;
for (item in exports.services) {
	if (exports.services.hasOwnProperty(item)) {
		service = item.substring(0, item.indexOf('-'));
		if (!exports[service]) {
			// create if doesn't exist
			exports[service] = {};
		}
		list = exports.services[item];
		for (i = 0; i < list.length; i++) {
			exports[service][list[i].name] = list[i];
			exports[service][list[i].name]['version'] = item.substring(item.indexOf('-') + 1);
		}
	}
}
