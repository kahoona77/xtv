'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('DownloadsCtrl', ['$scope', '$timeout', 'xtvService', function($scope, $timeout, xtvService) {

    $scope.downloads = [];

    $scope.loadDownloads = function () {
        xtvService.send('xtv.listDownloads').then(function(response){
            if (response.status == 'ok') {
                $scope.downloads = response.results;
            } else {
                msg.error (response.message);
            }
        });
    };
    $scope.loadDownloads();

    $scope.startReloadTimer = function () {
      $timeout (function () {
          $scope.loadDownloads();
          $scope.startReloadTimer();
      }, 3000)
    };
    $scope.startReloadTimer();

    $scope.selectItem = function (item) {
      $scope.selectedItem = item;
    };

    $scope.stop = function () {
        xtvService.send('xtv.stopDownload', {data: $scope.selectedItem}).then(function(response){
            if (response.status == 'ok') {
                $scope.selectedItem = undefined;
                $scope.loadDownloads();
            } else {
                msg.error (response.message);
            }
        });
    };

    $scope.resume = function () {
        xtvService.send('xtv.resumeDownload', {data: $scope.selectedItem}).then(function(response){
            if (response.status == 'ok') {
                $scope.selectedItem = undefined;
                $scope.loadDownloads();
            } else {
                msg.error (response.message);
            }
        });
    };

    $scope.showCancelConfirm = function () {
      $('#confirmDialog').modal ('show');
    };

    $scope.cancel = function () {
        xtvService.send('xtv.deleteDownload', {data: $scope.selectedItem}).then(function(response){
            if (response.status == 'ok') {
                $scope.selectedItem = undefined;
                $scope.loadDownloads();
            } else {
                msg.error (response.message);
            }
        });
    };

    $scope.clear = function () {
      var completed = [];
      angular.forEach ($scope.downloads, function (item) {
         if (item.status == 'COMPLETE') {
           completed.push (item);
         }
      });

      angular.forEach (completed, function (item) {
          xtvService.send('xtv.deleteDownload', {data: item}).then(function(response){
              if (response.status == 'ok') {
                  $scope.selectedItem = undefined;
                  $scope.loadDownloads();
              } else {
                  msg.error (response.message);
              }
          });
      });
    };

    $scope.calcTimeRemaining = function (item) {
      var remainingKBytes = (item.size - item.bytesReceived) / 1024;
      var remainingSeconds = remainingKBytes / item.speed;

      var min = Math.floor(remainingSeconds / 60);
      var sec = Math.round(remainingSeconds % 60);

      if (min < 10) {
        min = '0' + min
      }

      if (sec < 10) {
        sec = '0' + sec
      }

      return min + ":" + sec + " Minutes";
    };
  }]);