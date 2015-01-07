package grails.plugin.boselecta



class AutoCompleteService {

	static transactional = false
	def grailsApplication


	def returnControllerList() {
		def clazz=grailsApplication?.controllerClasses?.logicalPropertyName
		def results = clazz?.collect {[	'id': it, 'name': it ]}?.unique()
		return results
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
