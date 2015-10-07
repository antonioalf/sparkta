(function () {
  'use strict';

  /*NEW OPERATOR MODAL CONTROLLER */
  angular
    .module('webApp')
    .controller('NewOperatorModalCtrl', NewOperatorModalCtrl);

  NewOperatorModalCtrl.$inject = ['$modalInstance', 'operatorName', 'operatorType', 'operators', 'UtilsService', 'template'];

  function NewOperatorModalCtrl($modalInstance, operatorName, operatorType, operators, UtilsService, template) {
    /*jshint validthis: true*/
    var vm = this;

    vm.ok = ok;
    vm.cancel = cancel;

    init();

    function init() {
      vm.operator = {};
      vm.operator.name = operatorName;
      vm.operator.configuration = "";
      vm.operator.type = operatorType;
      vm.configHelpLink = template.configurationHelpLink;
      vm.error = false;
      vm.errorText = "";
      setDefaultConfiguration();
    }

    ///////////////////////////////////////

    function isValidConfiguration() {
      var configuration = vm.operator.configuration;
      try {
        vm.operator.configuration = JSON.parse(configuration);
        return true;
      } catch (e) {
        vm.errorText = "_POLICY_._CUBE_._INVALID_CONFIG_";
        vm.operator.configuration = configuration;
        return false;
      }
    }

    function isRepeated() {
      var position = UtilsService.findElementInJSONArray(operators, vm.operator, "name");
      var repeated = position != -1;
      if (repeated) {
        vm.errorText = "_POLICY_._CUBE_._OPERATOR_NAME_EXISTS_";
      }
      return repeated;
    }

    function setDefaultConfiguration() {
      var defaultConfiguration = '{}';
      var countType = template.functionNames[2];
      if (vm.operator.type !== countType) {
        defaultConfiguration = JSON.stringify(template.defaultOperatorConfiguration, null, 4);
      }
      vm.operator.configuration = defaultConfiguration;
    }

    function ok() {
      vm.errorText = "";
      if (vm.form.$valid) {
        if (!isRepeated() && isValidConfiguration()) {
          $modalInstance.close(vm.operator);
        }
      } else {
        vm.errorText = "_GENERIC_FORM_ERROR_";
      }
    };

    function cancel() {
      $modalInstance.dismiss('cancel');
    };
  };

})();
