
<g:javascript>
var loggedInUsers=[];
var user="${user }";
var receivers="${receivers}"
	var arrayLength = receivers.length;
// Connect websocket and set up processes 

<g:if test="${addAppName=='no'}">
var uri="ws://${hostname}/${chatApp }/${job}";
</g:if>
<g:else>
var uri="ws://${hostname}/${appName}/${chatApp }/${job}";
</g:else>

var webSocket=new WebSocket(uri);
webSocket.onopen=function(message) {processOpen(message);};
webSocket.onclose=function(message) {processClose(message);};
webSocket.onerror=function(message) {processError(message);};
webSocket.onmessage=function(message) {processMessage(message);	};


var userList=[];

function processMessage( message) {

	var jsonData = JSON.parse(message.data);
	if (jsonData.message!=null) {

		var itJson=isJson(jsonData.message);
		if (itJson==true	) {
		
			var jsonData1 = JSON.parse(jsonData.message);
			var setId,appendValue, appendName, updated='';
			
			if (jsonData1.updateThisDiv!=null) {
				setId=jsonData1.updateThisDiv;
			}
			
			if (jsonData1.appendValue!=null) {
				appendValue=jsonData1.appendValue;
			}
			
			if (jsonData1.appendName!=null) {
				appendName=jsonData1.appendName;
			}
			
			if (jsonData1.updated!=null) {
				updated=jsonData1.updated;
			}
			
			if (jsonData1.result!=null) {
			    var jsonResult = jsonData1.result;
			    updateView(jsonResult, setId, appendName, appendValue, updated);
			}
			 
			if (jsonData1.result3!=null) {
			    var jsonResult = jsonData1.result3;
			    updateOtherView(jsonResult,3);
			}
			
			if (jsonData1.result4!=null) {
			    var jsonResult = jsonData1.result4;
			    updateOtherView(jsonResult,4);
			}
			
			if (jsonData1.result5!=null) {
			    var jsonResult = jsonData1.result5;
			    updateOtherView(jsonResult,5);
			}
			
			if (jsonData1.result6!=null) {
			    var jsonResult = jsonData1.result6;
			    updateOtherView(jsonResult,6);
			}
			
			if (jsonData1.result7!=null) {
			    var jsonResult = jsonData1.result7;
			    updateOtherView(jsonResult,7);
			}
			
			if (jsonData1.result8!=null) {
			    var jsonResult = jsonData1.result8;
			    updateOtherView(jsonResult,8);
			}
			
			if (jsonData1.result9!=null) {
			    var jsonResult = jsonData1.result9;
			    updateOtherView(jsonResult,9);
			}
		}	
	}

	// Log out user if system tells it to	
	if (jsonData.system!=null) {
		if (jsonData.system=="disconnect") { 
			webSocket.send("DISCO:-"+user);
			webSocket.close();
		}
	}
}


function updateOtherView(jsonResult,cid) {
	jsonResult.forEach(function(entry1) {
	
		var rselect = document.getElementById(entry1.setId+cid);
	
		var opt = document.createElement('option');
		if (entry1.id!=null) {
			id=entry1.id;
		}
		if (entry1.name!=null) {
			name=entry1.name;
		}
		opt.value=name
		opt.text=id
		try {
			rselect.add(opt, null)
		} catch(ex) {
			rselect.add(opt)
		}
	});
}
 
function updateView(jsonResult, setId, appendName, appendValue, updated) {
	var id, name='';
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
		if (entry1.name!=null) {
			name=entry1.name;
		}
		opt.value=name
		opt.text=id
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
	webSocket.send("DISCO:-"+user);
	console.log('Closing');
	webSocket.close();
}


// Open connection only if we have frontuser variable    
function processOpen(message) {
	<g:if test="${frontuser}">
	webSocket.send("CONN:-${frontuser}");
	</g:if>
	<g:else>
	webSocket.send("DISCO:-");
	webSocket.close();
	</g:else>
}



window.onbeforeunload = function() {
	webSocket.onclose = function() { }
	webSocket.close();
}
</g:javascript>