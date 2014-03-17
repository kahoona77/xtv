'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('SettingsCtrl', ['$scope', 'vertxEventBusService', function($scope, vertxEventBusService) {

        $scope.settings = {
          nick:        'kahoona',
          port:        8080,
          tempDir:     '/temp',
          downloadDir: '/download'
        };

        $scope.saveSettings = function () {
          //TODO save settings
          $scope.hideSettingsDialog();
        };

        $scope.$on ('xtv:showSettingsDialog', function (){
           $scope.showSettingsDialog();
        });

        $scope.showSettingsDialog = function () {
            $('#settingsDialog').modal ('show');
        };

        $scope.hideSettingsDialog = function () {
            $('#settingsDialog').modal ('hide');
        };

  }]);