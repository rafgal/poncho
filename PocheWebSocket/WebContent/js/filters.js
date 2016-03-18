/**
 * 
 */

var poncho = angular.module('poncho_filters', []);

poncho.filter('ordinal', function() {
	return function(value, type, boardStatus) {
		if (boardStatus == 0) {
			if (value < 0) {
				return "Sin voto";
			} else {
				return "votó"
			}
		} else {
			if (value < 0) {
				return "Sin voto";
			}
			var typeString = '';
			switch (type) {
			case 0:
				typeString = ' horas';
				break;
			case 1:
				typeString = ' días';
				break;
			}
			return value + typeString;
		}
	}
});

poncho.filter('avg', function() {
	return function(value) {
		if (value < hoursPerDay) {
			return value + ' horas';
		} else {
			return value + ' horas (' + (value / hoursPerDay).toFixed(1) + ' días)';
		}

	}

});