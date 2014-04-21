'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('SettingsCtrl', ['$scope', 'xtvService', function($scope, xtvService) {

    $scope.loadSettings = function () {
      xtvService.send('xtv.loadSettings').then(function (response) {
        if (response.status == 'ok') {
          $scope.settings = response.result;

        } else {
          msg.error(response.message);
        }
      });
    };
    $scope.loadSettings();

    $scope.saveSettings = function () {
      xtvService.send('xtv.saveSettings', {data: $scope.settings}).then(function (response) {
        if (response.status = 'ok') {
          $scope.hideSettingsDialog();
          $scope.loadSettings();
        } else {
          msg.error(response.message);
        }
      });
    };

    $scope.$on('xtv:showSettingsDialog', function () {
      $scope.showSettingsDialog();
    });

    $scope.showSettingsDialog = function () {
      $('#settingsDialog').modal('show');
    };

    $scope.hideSettingsDialog = function () {
      $('#settingsDialog').modal('hide');
    };

  }]);