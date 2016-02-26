//This file register into WebSocket for votation

var ip_server = "localhost";// "10.10.1.90";
var port = 9091;
var poncho = angular.module('poncho', []);
var ws = new WebSocket('ws://' + ip_server + ':' + port
		+ '/PonchoWebSocket/ponchito');
var user = undefined;

ws.onmessage = function(response) {
	console.log(response);
	var message = JSON.parse(JSON.stringify(response.data));
	console.log(JSON.stringify(message));
	/*
	 * switch (key) { case value:
	 * 
	 * break;
	 * 
	 * default: break; }
	 */
	$("#register").hide();
	$("#votation").show();
}

poncho.controller("loginController", function($scope) {
	$scope.register = function(person) {
		if (person != "") {
			console.log(person);
			var jsonRegister = {"c":0,"items":[{"nombre":person}]};
			console.log(JSON.stringify(jsonRegister))
			ws.send(jsonRegister);
		}
	};
});

poncho.controller("voteController", function($scope) {
	$scope.submit = function() {
		var vote = $scope.vote;
		if (vote != undefined) {
			var json = JSON.stringify(vote);
			console.log(json);
		}
		// console.log(vote.quantity + " --- " + vote.type);
	};
});