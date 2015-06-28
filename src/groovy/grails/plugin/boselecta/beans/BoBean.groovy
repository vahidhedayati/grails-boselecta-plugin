package grails.plugin.boselecta.beans

import grails.util.Holders
import grails.validation.Validateable

@Validateable
class BoBean {

		String job
		String user
		String id
		
		String domain
		String searchField
		String collectField
		String bindid
		
		String domain2
		String searchField2
		String collectField2
		String setId = "selectPrimary"
		
		/* 
		 * If you exceed domain2 bean will complain use
		 * <bo:selecta2 instead of <bo:selecta
		 */
		 
		String name
		
		String appendValue
		String appendName
		String value
		String nextValue
		String placeHolder
		String hiddenField
		String jsonField
		String domainDepth
		String noSelection
		String dataList
		String sdataList
		
		def getDomainDepth() {
			int dd = 1
			if (domainDepth) {
				dd +=domainDepth as int
			}else{
		 		dd=getConfig('depth') ?: 4
			}
			return dd  
		}
		
		String max
		String order
		boolean norefPrimary = false
		boolean autoComplete = false
		boolean autoCompletePrimary = false
		boolean selectToAutoComplete = false
		boolean autoCompleteToSelect = false
		
		// Format can be set as JSON
		String formatting
		def getFormatting() {
			if (!formatting) { 
				formatting = getConfig('formatting') ?: 'none'
			}	
			return formatting
		}
		
		/* funky stuff to set
		 * search and collect fields
		 * for primary secondary 
		 */
		def getSearchField2() {
			if (!searchField2) {
				searchField2 = searchField
			}	
			return searchField2
		}
		def getCollectField2() {
			if (!collectField2){
				collectField2 = collectField
			}
			if (!collectField2) {
				collectField2 = searchField2
			}
			return collectField2
		}
		def getCollectField() {
			if (!collectField) {
				collectField = collectField2
			}
			return collectField
		}
		def getSearchField() {
			if (!searchField) {
				searchField = searchField2
			}
			return searchField
		}
		
		def getName() { 
			if (!name) {
				name = id
			}
			return name
		}
	
		def getAppendName() { 
			if ((appendValue)&&(!appendName)) {
				appendName='Values Updated'
			}
			return appendName
		}	
		
		boolean require = false
		boolean requireField=true
		def getRequireField() { 
			if (require) {
				requireField=require
			}
			return requireField
		}
		def getDataList() {
			dataList = "${id}-datalist"
			return dataList
		} 
		
		def getSdataList() { 
			sdataList = "${setId}-datalist"
			return sdataList
		}
		
		List primarylist = []
		boolean loadPrimary = false
	
	
	static contstraints = {
		noSelection nullable: true
		domain nullable: true
		domain2 nullable: true
		searchField2: nullable: true
		collectField2: nullable: true
		searchField: nullable: true
		collectField: nullable: true
		job (nullable: false, validator:validateInput)
		user (nullable: false, validator:validateInput)
	}

	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
	
	def getConfig(String configProperty) {
		Holders.config.boselecta[configProperty] ?: ''
	}
}
