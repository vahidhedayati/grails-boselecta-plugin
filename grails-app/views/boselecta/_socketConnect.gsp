
<g:javascript>
	var loggedInUsers=[];
	var user="${user }";


<g:if test="${addAppName=='no'}">
	var uri="ws://${hostname}/${chatApp }/${job}";
</g:if>
<g:else>
	var uri="ws://${hostname}/${appName}/${chatApp }/${job}";
</g:else>

	var webSocket${job}=new WebSocket(uri);
	webSocket${job}.onopen=function(message) {processOpen(message);};
	webSocket${job}.onclose=function(message) {processClose(message);};
	webSocket${job}.onerror=function(message) {processError(message);};
	webSocket${job}.onmessage=function(message) {processMessage(message);	};


	var userList=[];

	function processMessage( message) {
	
		//console.log(JSON.stringify(message.data));
		
		var jsonData = JSON.parse(message.data);
		if (jsonData.message!=null) {
			var itJson=isJson(jsonData.message);
			if (itJson==true	) {
			
				var jsonData1 = JSON.parse(jsonData.message);
				var appendValue, appendName, updated, updateValue, nextValue,formatting='';
				
				if (jsonData1.updateThisDiv!=null) {
					setId=jsonData1.updateThisDiv;
				}
				
				if (jsonData1.appendValue!=null) {
					appendValue=jsonData1.appendValue;
				}
				
				if (jsonData1.appendName!=null) {
					appendName=jsonData1.appendName;
				}
				
				if (jsonData1.formatting!=null) {
					formatting=jsonData1.formatting;
					
				}
				
				
				if (jsonData1.nextValue!=null) {
					nextValue=jsonData1.nextValue;
				}
				
				if (jsonData1.updated!=null) {
					updated=jsonData1.updated;
				}
				
				if (jsonData1.updateValue!=null) {
					updateValue=jsonData1.updateValue;
				}
				
				if (jsonData1.result!=null) {
				    var jsonResult = jsonData1.result;
				    updateView(jsonResult, setId, appendName, appendValue, updated, updateValue, nextValue,formatting);
				}
				
				for (a=3; a < 10; a++) {
				var c=a;
				try {
					var cid = eval('jsonData1.setId'+c);
					 if (cid!=null) {
					 	var jsonResult = eval('jsonData1.result'+c);
					 	updateOtherView(jsonResult, cid, updateValue,formatting);
					}
				} catch(ex) {
				}	
				}
			}	
		}

	// Log out user if system tells it to	
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket${job}.send("DISCO:-"+user);
			webSocket${job}.close();
		}
	}
}


function updateOtherView(jsonResult,cid, updateValue,format) {
	var id, name,resarray='';
	jsonResult.forEach(function(entry) {
		var rselect = document.getElementById(cid);
		if (rselect) {
			var opt = document.createElement('option');
			if (entry.id!=null) {
				id=entry.id;
			}
			if (entry.name!=null) {
				name=entry.name;
			}
			
			
			if (format == "JSON") {
				opt.value=JSON.stringify(resarray);
			}else{
				opt.value=name;
			}
			opt.text=id
			if (id==updateValue) {
				//opt.checked=true;
				opt.setAttribute('selected', true);
			}
			try {
				rselect.add(opt, null)
			} catch(ex) {
				rselect.add(opt)
			}
			
		}
	});
}
 
function updateView(jsonResult, setId, appendName, appendValue, updated, updateValue, nextValue,format) {
	var id, name,resarray='';
	var rselect = document.getElementById(setId);
	var l = rselect.length;
	while (l > 0) {
		l--
		rselect.remove(l);
	}

	if ((appendName!="")&&(updated=="yes")) { 	
		var opt = document.createElement('option');
		opt.value=appendValue;
		opt.text=appendName;
		try {
			rselect.add(opt, null);
		} catch(ex) {
			rselect.add(opt);
		}
	}
				
	jsonResult.forEach(function(entry1) {
		var opt = document.createElement('option');
		if (entry1.id!=null) {
			id=entry1.id;
		}
		if (entry1.resarray!=null) {
			resarray=entry1.resarray;
		}
		if (entry1.name!=null) {
			name=entry1.name;
		}
		if (format == "JSON") {
				opt.value=JSON.stringify(resarray);
			}else{
				opt.value=name;
			}
		opt.text=id
			if (name==nextValue) {
				opt.setAttribute('selected', true);
			}
		try {
			rselect.add(opt, null)
		} catch(ex) {
			rselect.add(opt)
		}
	});
} 

function isJson(message) {
	var input='{';
	return new RegExp('^' + input).test(message);
}
function isPm(message) {
	var input='/pm';
	return new RegExp('^' + input).test(message);
}

function isReceivedMsg(message) {
	var input='_received';
	return new RegExp( input +'$').test(message);
}

function processError(message) {
	console.log(message);
}

function verifyIsOn(uid) {
	var ison="false";
	var idx = loggedInUsers.indexOf(uid);
	if (idx != -1) {
		ison="true";
	}
	return ison;
}	

function addUser(uid) {
	var idx = loggedInUsers.indexOf(uid);
	if(idx == -1) {
		loggedInUsers.push(uid);
	}	
}

function processClose(message) {
	webSocket${job}.send("DISCO:-"+user);
	webSocket${job}.close();
}


// Open connection only if we have frontuser variable    
function processOpen(message) {
	<g:if test="${frontuser}">
		webSocket${job}.send("CONN:-${frontuser}");
	</g:if>
	<g:else>
		webSocket${job}.send("DISCO:-");
		webSocket${job}.close();
	</g:else>
}



window.onbeforeunload = function() {
	webSocket${job}.onclose = function() { }
	webSocket${job}.close();
}
</g:javascript>