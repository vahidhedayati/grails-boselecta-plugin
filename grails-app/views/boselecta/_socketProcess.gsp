<g:javascript>


function actionThis(value, divid, user) {
	webSocket.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value}));
}
</g:javascript>