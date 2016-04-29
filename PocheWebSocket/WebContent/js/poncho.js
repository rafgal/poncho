(function() {
	
	var poncho = angular.module('poncho', [ 'ngMaterial','ngMessages','d3', 'poncho_directives', 'poncho_filters' ]);
	
	poncho.controller("ponchoController", function($scope, $mdDialog){
		$scope.welcomeText='img/poncho.png';
		$("#ino").hide();
		ws.onmessage = function(response) {
			console.log(response.data);
			var data =JSON.parse(response.data)
			var commando = data.comando;
			if(commando == 3) {
				$scope.boards = data.salas;
				$scope.$apply();
			} else if(commando == 4) {
				console.log("Se ha borrado la sala");
			} else if(commando == 5) {
				console.log("Se ha actualizado la sala");
				$scope.changeToBoard(true);
				$scope.$broadcast('updateBoard', data.board);
			}else if (commando == 7) {
				localStorage.setItem(PONCHO_SESSION_ID_KEY,
						data.ponchoSessionId);
			} else {
				console.log("other command " + commando);
			}
		}
		
		$scope.logout = function(ev){
			var confirm = $mdDialog.confirm()
			.title('Salir de la sala')
			.textContent('Si sale de la sala puede afectar la votación actual!!')
			.ariaLabel('Lucky day')
			.targetEvent(ev)
			.ok('Quiero salir')
			.cancel('Me arrepiento');
			$mdDialog.show(confirm).then(function() {
				$scope.changeToBoard(false);
				$scope.changeToCreate(false);
				ws.send('{"comando":6}');
			});
		};
		
		$("#board-view").hide();
		$scope.changeToBoard = function(change){
			if(change){
				$("#login-view").hide();
				$("#board-view").show();
			} else {
				$("#login-view").show();
				$("#board-view").hide();
			}
		};
		
		$scope.changeToCreate = function(change){
			if(change) {
				$("#boards-view").hide();
				$("#create-board-view").show();
			} else {
				$("#boards-view").show();
				$("#create-board-view").hide();
			}
		};
		
		$scope.changeToPrincipal = function(board){
			$("#boards-view").show();
			$("#create-board-view").hide();
			$("#ino").hide();
		};
	});
	
	
	poncho.controller("loginController", function($scope, $http) {
		
		$scope.register = function(person, board) {
			if ( person != undefined && board != undefined) {
				$("#ino").hide();
				ws.send('{"comando":0,"nombre":"' + person + '", "room":"' + board + '"}');
			}else{
				$("#ino").show();
			}
		};
	});

	poncho.controller("BoardController", function($scope, $http) {
		
		var boardCtrl = $scope;

		boardCtrl.fields = {
			type : 0
		};
		boardCtrl.command = {
			comando : 1
		};
		boardCtrl.usersBoard = [];
		boardCtrl.status = -1;
		boardCtrl.approved = false;
		
		boardCtrl.resetRoom = function() {
			boardCtrl.command.comando = 8;
			delete boardCtrl.command.vote;
			ws.send(JSON.stringify(boardCtrl.command));
			boardCtrl.fields.vote = null;
			boardCtrl.fields.type = 0;
//			boardCtrl.votoForm.value.$setPristine();
//			boardCtrl.votoForm.value.$setUntouched();
//			boardCtrl.votoForm.value.$error={};
		};
		
		$scope.$on('updateBoard', function (event, data) {
			$("#votation").show();
			boardCtrl.usersBoard = data.usuarios;
			boardCtrl.status = data.boardStatus;
			if (boardCtrl.status === 0) {
				boardCtrl.approved = false;
			} else {
				boardCtrl.fields.vote = null;
				boardCtrl.fields.type = 0;
				var sum = 0;
				for (var i = 0; i < boardCtrl.usersBoard.length; i++) {
					var factor = 1;
					if (boardCtrl.usersBoard[i].tipoVoto === 1) {
						factor = hoursPerDay;
					}
					sum += boardCtrl.usersBoard[i].voto * factor;
				}
//				boardCtrl.avg = sum / boardCtrl.usersBoard.length;
				boardCtrl.avg = Math.ceil(sum/boardCtrl.usersBoard.length/0.5)*0.5;
				boardCtrl.std=standardDeviation(boardCtrl.usersBoard);
				boardCtrl.usersBoard.sort(usersSortFunction);
				boardCtrl.data = boardCtrl.usersBoard;
			}
			$scope.$apply();
		});
		
		boardCtrl.vote = function() {
			boardCtrl.command.comando = 1;
			boardCtrl.command.vote = {};
			boardCtrl.command.vote.value = boardCtrl.fields.vote;
			boardCtrl.command.vote.type = boardCtrl.fields.type;
			ws.send(JSON.stringify(boardCtrl.command));
		}
	});
	
	poncho.controller('ListCtrl', function($mdDialog) {
		this.navigateTo = function(to, event) {
		    $mdDialog.show(
		      $mdDialog.alert()
		        .title('Navigating')
		        .textContent('Imagine being taken to ' + to)
		        .ariaLabel('Navigation demo')
		        .ok('Neat!')
		        .targetEvent(event)
		    );
		  };
	});
	
	
})();