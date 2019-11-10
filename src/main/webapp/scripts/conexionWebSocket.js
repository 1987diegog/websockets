$(document).ready(function() {

	var uriWS = "ws://localhost:8080/websockets/currentDateTime";
	
	var myWebsocket = new WebSocket(uriWS);
	
	console.log(myWebsocket);

	myWebsocket.onopen = function(event) {
		console.log("Open: connected...");
		miWebsocket.send("Hi there...");
	}
	
	myWebsocket.onmessage = function(event) {
		console.log(event.data);
	}
	
});