

<g:javascript>
function actionThis(value, divid, user, cjobName) {
	webSocket${job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value, cjobName: cjobName}));
}

function actionNonAppendThis(value, divid, user, cjobName) {
	webSocket${job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value, updated: 'no', cjobName: cjobName}));
}

</g:javascript>



