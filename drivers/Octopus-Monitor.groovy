/**
 *
 *  File: Octopus-Monitor.groovy
 *  Platform: Hubitat
 *
 *  Requirements:
 *     1) To retrieve power costs from Octopus Energy
 *     2) To send an event when the cost goes past a configured threshold
 *
 *  Copyright 2021 David Irwin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2021-03-05  David Irwin    Creation
 *    2021-03-06  David Irwin    Tidy up, create actions, test auth against API (failing)
 *    2021-03-07  David Irwin    Sort out file header
 * 
 */

 def version() {"v0.0.20210307"}

metadata {
    definition (name: "Agile Octopus Monitor", namespace: "firstrulez", author: "David Irwin", description: "This driver will provice simple notifications when Agile Octopus eletricity costs change", importUrl: "https://raw.githubusercontent.com/FirstRulez/Hubitat-Octopus/main/drivers/Octopus-Monitor.groovy") {
        capability "Initialize"
        capability "Refresh"
    }
}

preferences {
    input "apiSecret", "text", title: "Agile Octopus API Secret Key", description: "in form of sk_live_acbDEF123ACBdef321", required: true, displayDuringSetup: true
    input "accountNumber", "text", title: "Agile Octopus Account Number", description: "in form of A-1A2B3C4D", required: true, displayDuringSetup: true
	input "pollingInterval", "number", title: "Polling Interval", description: "in minutes", range: "1..30", defaultValue: 30, displayDuringSetup: true
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
}

def logsOff(){
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def refresh() {
   log.warn "refresh called, notthing defined..."
}

def installed() {
    state.version = version()
    log.info "installed() called"
    if (apiSecret) {
        if (accountNumber) {
			initialize()
		}
		else
		{
			log.warn "Please enter the Account Number and then click SAVE"
		}
    }
    else
    {
        log.warn "Please enter the API Key and then click SAVE"
    }
	log.info "Creating schedule"
    schedule('0 */${pollingInterval} * ? * *', getCurrentCost)
}

def updated() {
    state.version = version()
    log.info "updated() called"
    unschedule()
    if (logEnable) runIn(1800,logsOff)
    initialize()
	log.info "Creating schedule"
    schedule('0 */${pollingInterval} * ? * *', getCurrentCost)
}

def initialize() {
    state.version = version()
    log.info "initialize() called"
    if (apiSecret) {
        if (accountNumber) {
			def params = [
				uri: "https://api.octopus.energy/v1/accounts/",
				timeout: 10,
				headers: [Authorization: "Basic ${apiSecret}:"],
			]
			httpGet(params){response ->
				if(response.status != 200) {
					log.debug "Things went badly on the API call"
					log.debug "Response status was ${response.status}" 
				}
				else {
					log.debug "Response from IoTaWatt = ${response.data}"
				}
			}
		}
		else
		{
			log.warn "Please enter the Account Number and then click SAVE"
		}
    }
    else
    {
        log.warn "Please enter the API Key and then click SAVE"
    }
}

def getCurrentCost() {
	log.info "Executing 'getCurrentCost()'"
}

def uninstalled() {
    log.info "Executing 'uninstalled()'"
    unschedule()
}
