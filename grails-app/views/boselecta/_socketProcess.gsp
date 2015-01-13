

<g:javascript>
	function actionThis(value, divid, user, cjobName) {
		webSocket${job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value, cjobName: cjobName}));
	}

	function actionNonAppendThis(value, divid, user, cjobName) {
		webSocket${job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value, updated: 'no', cjobName: cjobName}));
	}

	function updateList(value, id, dataList, divid) {
		webSocket${job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateList : dataList, updateAutoValue : value,  cId: id}));
	}

	//function updatePlaceHolder(value, id, dataList) {
 		//var input = document.getElementById(id);
		//var dataList = document.getElementById(dataList);
		//var x = input.value;
        //var val = $(dataList).find('option[value="' + x + '"]');
        //var endval = val.attr('id');
 	//}

</g:javascript>



