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
      // TODO clearCompleted downloads
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


//    $scope.downloads.push ({
//      pos:       1,
//      status:    'RUNNING',
//      file:      'Justified.S05E08.720p.mkv',
//      loaded:    479199232,
//      size:      1390411776,
//      speed:     489,
//      remaining: 23
//    });
//
//    $scope.downloads.push ({
//      pos:       2,
//      status:    'FAILED',
//      file:      'Shameless.S04E05.720p.mkv',
//      loaded:    289199232,
//      size:      1158676480,
//      speed:     156,
//      remaining: 52
//    });
//
//    $scope.downloads.push ({
//      pos:       3,
//      status:    'RUNNING',
//      file:      'Community.S04E05.HDTV.avi',
//      loaded:    186646528,
//      size:      193986560,
//      speed:     347,
//      remaining: 3
//    });
//
//    $scope.downloads.push ({
//      pos:       4,
//      status:    'COMPLETE',
//      file:      'Community.S04E04.HDTV.avi',
//      loaded:    193986560,
//      size:      193986560,
//      speed:     62,
//      remaining: 0
//    });

  }]);