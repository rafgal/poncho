//This file register into WebSocket for votation
var hoursPerDay = 7;
var ws = new WebSocket('ws://' + window.location.hostname + ':'
		+ window.location.port + window.location.pathname + 'ponchito');
var usersSortFunction = function(a, b) {
	return a.voto - b.voto;
};
window.onbeforeunload = closingCode;
function closingCode() {
	ws.close();
	return null;
}

function changeViewCreateRoom(){
	$("#list-boards").hide();
	$("#create-board").show();
}