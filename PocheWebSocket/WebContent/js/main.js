//This file register into WebSocket for votation
const PONCHO_SESSION_ID_KEY="ponchoSessionId";
var hoursPerDay = 7;
var ws = new WebSocket('ws://' + window.location.hostname + ':'
		+ window.location.port + window.location.pathname + 'ponchito');
ws.onopen = function(e) {
	checkPonchoSession();
};
var usersSortFunction = function(a, b) {
	return a.voto - b.voto;
};
window.onbeforeunload = closingCode;
function closingCode() {
	ws.close();
	return null;
}
function checkPonchoSession() {
	console.log("ck sssion")
	if (localStorage.getItem(PONCHO_SESSION_ID_KEY) !== null) {
		var message = {};
		message.comando = 0;
		message.ponchoSessionId = localStorage.getItem(PONCHO_SESSION_ID_KEY);
		ws.send(JSON.stringify(message));
		console.log("sended")
	}
}