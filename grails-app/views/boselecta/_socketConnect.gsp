
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
			var setId=''
				if (jsonData1.updateThisDiv!=null) {
					setId=jsonData1.updateThisDiv
				}

			if (jsonData1.result!=null) {

				var id, name=''
					var rselect = document.getElementById(setId)
					var l = rselect.length
					while (l > 0) {
						l--
						rselect.remove(l)
					}

				jsonData1.result.forEach(function(entry1) {
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
	$('#chatMessages').append(user+" disconnecting from server... \n");
	console.log('Closing');
	webSocket.close();
}


// Open connection only if we have frontuser variable    
function processOpen(message) {
	<g:if test="${frontuser}">
	webSocket.send("CONN:-${frontuser}");
	</g:if>
	<g:else>
	$('#chatMessages').append("Chat denied no username \n");
	webSocket.send("DISCO:-");
	webSocket.close();
	</g:else>
}



window.onbeforeunload = function() {
	webSocket.onclose = function() { }
	webSocket.close();
}
</g:javascript>