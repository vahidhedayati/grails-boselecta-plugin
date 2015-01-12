package grails.plugin.boselecta

import grails.converters.JSON



class AutoCompleteService {

	static transactional = false
	def grailsApplication


	def returnControllerList() {
		def clazz=grailsApplication?.controllerClasses?.logicalPropertyName
		def results = clazz?.collect {[	'id': it, 'name': it ]}?.unique()
		return results
	}

	def autocompletePrimaryAction (String domain, String searchField, String collectField, String order,
			String max, String term) {
		def primarySelectList=[]
	
		println "autocompletePrimaryAction: ${domain} ${searchField} ${collectField} ${order} ${max} ${term}"
		def domainClass = grailsApplication.getDomainClass(domain).clazz
		domainClass.withTransaction {
			def query = domainClass.withCriteria {
				or{
					ilike searchField, term + '%'
				}
				projections {
					property(collectField)
					property(searchField)
				}
				maxResults(Integer.parseInt(max,10))
				//order(searchField,order)
			}
			if (query.size() < 5 ) {
				query = domainClass.withCriteria {
					or{
						ilike searchField, "%${term}%"
					}
					projections {
						property(collectField)
						property(searchField)
					}
					maxResults(Integer.parseInt(max,10))
					//order(searchField, order)
				}
			}
			if (query) {
				primarySelectList=resultSet1(query as List)
			}
		}
		return primarySelectList
	}


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
				primarySelectList=resultSet2(query as List)
			}
		}
		return primarySelectList
	}

	def resultSet1(def results) {
		def primarySelectList=[]
		if (results) {
			results.each {
				def primaryMap = [:]
				primaryMap.put('id', it[0])
				primaryMap.put('label', it[1])
				primaryMap.put('resarray', [selectedText: it[0], selected:it[1]])
				primarySelectList.add(primaryMap)
			}
		}
		return primarySelectList
	}

	def resultSet2(def results) {
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
		println "--- ${className} ${searchField} ${collectField}"
		def results
		if (!className.equals('')) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			clazz.withTransaction { 
				def res = clazz.findAll()
				res.each { 
					println "$it ---> -->"
				}
				results = res?.collect {[	'id': it."${searchField}", 'label': it."${collectField}" ]}?.unique()
			}
			return results
			
			//clazz?.list()
		}
	}
	
	def parseFilter(def filter,def filterType) {
		def myfilter='%'+filter+'%'
		if (filterType.equals('S')) {
			myfilter=filter+'%'
		} else if (filterType.equals('E')) {
			myfilter='%'+filter
		}
		return myfilter
	}



}
