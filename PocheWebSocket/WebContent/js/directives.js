/**
 * 
 */

var poncho = angular.module('poncho_directives', []);

poncho.directive('radioWithChangeHandler',
		[ function checkboxWithChangeHandler() {
			return {
				replace : false,
				require : 'ngModel',
				scope : false,
				link : function(scope, element, attr, ngModelCtrl) {
					$(element).change(function() {
						if (element[0].checked) {
							scope.$apply(function() {
								ngModelCtrl.$setViewValue(attr.value);
							});
						}
					});
				}
			};
		} ]);

poncho.directive('bootstrapSwitch', [ function() {
	return {
		restrict : 'A',
		require : '?ngModel',
		link : function(scope, element, attrs, ngModel) {
			element.bootstrapSwitch();

			element.on('switchChange.bootstrapSwitch', function(event,
					state) {
				if (ngModel) {
					scope.$apply(function() {
						ngModel.$setViewValue(state);
					});
				}
			});

			scope.$watch(attrs.ngModel, function(newValue, oldValue) {
				if (newValue) {
					element.bootstrapSwitch('state', true, true);
				} else {
					element.bootstrapSwitch('state', false, true);
				}
			});
		}
	};
} ]);