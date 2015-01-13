
<input type="text" id="${id}" 
onChange="<g:if test="${autoCompleteToSelect }">actionThis(this.value, '${setId}', '${user}', '${job}');</g:if>
<g:else>updateList(this.value, '${id}', '${sDataList}', '${setId}')</g:else>"
  list="${dataList}" name = "${name}" placeholder="${placeHolder ?: 'AutoComplete' }">
 
  
<datalist id="${dataList}"></datalist>


