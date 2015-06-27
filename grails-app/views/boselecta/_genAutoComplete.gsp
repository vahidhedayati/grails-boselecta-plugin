
<input type="text" name="${id}" id="${id}"  onclick="this.value='';clearAll('${setId}');"
onChange="<g:if test="${autoCompleteToSelect }">actionThis(this.value, '${setId}');</g:if>
<g:else>updateList(this.value, '${id}', '${sDataList}', '${setId}')</g:else>"
  list="${dataList}" name = "${name}" placeholder="${placeHolder ?: 'AutoComplete' }"/>
 
<g:hiddenField name="${hiddenField?:'HIDDEN_'}${id}" value=""/>
<g:hiddenField name="${jsonField?:'JSON_'}${id}" value=""/>
  
<datalist id="${dataList}"></datalist>


<script>
	function getHiddenValue${id}() {
		return  $('${hiddenField?:'HIDDEN_'}${id}').val();
	}
	function clearAll(setId) {
		$("#"+setId).val('');
	}
	$('#${id}').change(function(){
    	var c =  $('#${id}').val();
    	$('#${id}').val(getTextValue${id}(c));
    	$('#${hiddenField?:'HIDDEN_'}${id}').val(c);
	});
	function getTextValue${id}(c){
  		var listing = $('#${dataList}');
  		var endVal = $(listing).find('option[value="'+ c + '"]');
  		var dataValue = $('#${dataList} [value="' + c + '"]').data('value');
  		if (dataValue) {
	  		$('#${jsonField?:'JSON_'}${id}').val(JSON.stringify(dataValue));
  		}
  		return endVal.text();  
	}	
</script>