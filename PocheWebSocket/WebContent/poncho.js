//This file register into WebSocket for votation

var ip_server = "localhost";// "10.10.1.90";
var port = 9091;
var poncho = angular.module('poncho', []);
var ws = new WebSocket('ws://' + ip_server + ':' + port
		+ '/PonchoWebSocket/ponchito');
var user = undefined;

ws.onmessage = function(response) {
	var str = response.data;
	var json = JSON.parse(str);
	console.log(json.usuarios);
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
			var jsonRegister = {"c":0,"nombre":person};
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