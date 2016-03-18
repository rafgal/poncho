(function() {
	
	var poncho = angular.module('poncho', [ 'ngMaterial','d3', 'poncho_directives', 'poncho_filters', 'ui.bootstrap' ]);
	
	poncho.controller("loginController", function($scope, $http) {

		ws.onmessage = function(data) {
			$("#register").hide();
			$("#votation").show();
			var datas = angular.fromJson(data.data);
			$scope.updateBoard(JSON.parse(data.data));
		}
		$scope.register = function(person) {
			console.log("register")
			if (person != "") {
				ws.send('{"comando":0,"nombre":"' + person + '"}');
			}
		};
	});

	poncho.controller("BoardController", function($scope, $http) {

		var boardCtrl = this;
		boardCtrl.welcomeText='img/poncho.png';

		boardCtrl.fields = {
			type : 0
		};
		boardCtrl.command = {
			comando : 1
		};
		boardCtrl.board = [];
		boardCtrl.status = 0;
		boardCtrl.approved = false;
		boardCtrl.setConformity = function() {
			boardCtrl.command.comando = 2;
			delete boardCtrl.command.vote;
			boardCtrl.command.approved = boardCtrl.approved;
			ws.send(JSON.stringify(boardCtrl.command));
			boardCtrl.fields.vote = null;
			boardCtrl.fields.type = 0;
		};
		$scope.updateBoard = function(data) {
			boardCtrl.board = data.usuarios;
			boardCtrl.welcomeText='img/poncho2.png';
			console.log(boardCtrl.board);
			boardCtrl.status = data.boardStatus;
			if (boardCtrl.status === 0) {
				boardCtrl.approved = false;
			} else {
				var sum = 0;
				for (var i = 0; i < boardCtrl.board.length; i++) {
					var factor = 1;
					if (boardCtrl.board[i].tipoVoto === 1) {
						factor = hoursPerDay;
					}
					sum += boardCtrl.board[i].voto * factor;
				}
				boardCtrl.avg = sum / boardCtrl.board.length;
				boardCtrl.std=standardDeviation(boardCtrl.board);
				boardCtrl.board.sort(usersSortFunction);
				// hard-code data
				$scope.data = boardCtrl.board;
			}
			$scope.$apply();
		};
		$scope.vote = function() {
			boardCtrl.command.comando = 1;
			boardCtrl.command.vote = {};
			boardCtrl.command.vote.value = boardCtrl.fields.vote;
			boardCtrl.command.vote.type = boardCtrl.fields.type;
			console.log(JSON.stringify(boardCtrl.command));
			ws.send(JSON.stringify(boardCtrl.command));
		}
	});
})();