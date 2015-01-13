package grails.plugin.boselecta

import grails.converters.JSON



class AutoCompleteService {

	static transactional = false
	def grailsApplication

	// No reference selection method i.e. belongsTo=UpperClass
	ArrayList selectNoRefDomainClass(String domainClaz, String domainClaz2, String searchField, String collectField, String bindName, String recordId) {
		def primarySelectList = []
		if ((domainClaz2) && (domainClaz) &&( recordId)) {
			def domainClass2 = grailsApplication?.getDomainClass(domainClaz2)?.clazz
			def domainClass = grailsApplication?.getDomainClass(domainClaz)?.clazz
			domainClass2.withTransaction {
				def domaininq=domainClass?.get(recordId.toLong())
				if (domaininq) {
					domaininq."${bindName}".each { dq ->
						def primaryMap = [:]
						primaryMap.put('id',dq."${collectField}")
						primaryMap.put('name', dq."${searchField}")
						primaryMap.put('resarray', [selectedText: dq."${searchField}", selected:dq."${collectField}"])
						primarySelectList.add(primaryMap)
					}
				}
			}
		}
		return primarySelectList
	}

	ArrayList selectDomainClass(String domainClaz, String searchField, String collectField, String bindName, String recordId) {
		def primarySelectList=[]
		if (domainClaz && bindName) {
			def domainClass = grailsApplication?.getDomainClass(domainClaz)?.clazz
			def query = domainClass.withCriteria {
				eq  (bindName as String,  recordId.toLong())
				projections {
					property(collectField)
					property(searchField)
				}
				order(searchField)
			}
			if (query) {
				primarySelectList=resultSet(query as List)
			}
		}
		return primarySelectList
	}

	def resultSet(def results) {
		def primarySelectList=[]
		if (results) {
			results.each {
				def primaryMap = [:]
				primaryMap.put('id', it[0])
				primaryMap.put('name', it[1])
				primaryMap.put('resarray', [selectedText: it[0], selected:it[1]])
				primarySelectList.add(primaryMap)
			}
		}
		return primarySelectList
	}

	List returnPrimaryList(String className) {
		if (!className.equals('')) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			clazz?.list()
		}
	}

	def returnAutoList(String className, String searchField, String collectField) {
		def results
		if (className) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			clazz.withTransaction {
				def res = clazz.findAll()
				results = res?.collect {[	'id': it."${collectField}", 'name': it."${searchField}" ,
						'resarray': [it."${collectField}", it."${searchField}" ]]}?.unique()
			}
			return results
		}
	}
	
	/*
	 def returnPrimaryList(String className,String searchField, String collectField ) {
	 def results
	 if (className) {
		 Class clazz = grailsApplication?.getDomainClass(className)?.clazz
		 clazz.withTransaction {
			 def res = clazz.findAll()
			 results = res?.collect {[	'id': it."${collectField}", 'name': it."${searchField}" ,
					 'resarray': [it."${collectField}", it."${searchField}" ]]}?.unique()
		 }
		 return results
	 }
	 }*/
 



}
