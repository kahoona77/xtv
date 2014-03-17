'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('DownloadsCtrl', ['$scope', 'vertxEventBusService', function($scope, vertxEventBusService) {

    $scope.downloads = [];

    $scope.selectItem = function (item) {
      $scope.selectedItem = item;
    };

    $scope.stop = function () {
      // TODO stop download
    };

    $scope.resume = function () {
      // TODO resume download
    };

    $scope.showCancelConfirm = function () {
      $('#confirmDialog').modal ('show');
    };

    $scope.cancel = function () {
      // TODO cancel download
      var index = $scope.downloads.indexOf($scope.selectedItem);
      $scope.downloads.splice(index, 1);
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
        var index = $scope.downloads.indexOf(item);
        $scope.downloads.splice(index, 1);
      });
    };


    $scope.downloads.push ({
      pos:       1,
      status:    'RUNNING',
      file:      'Justified.S05E08.720p.mkv',
      loaded:    479199232,
      size:      1390411776,
      speed:     489,
      remaining: 23
    });

    $scope.downloads.push ({
      pos:       2,
      status:    'FAILED',
      file:      'Shameless.S04E05.720p.mkv',
      loaded:    289199232,
      size:      1158676480,
      speed:     156,
      remaining: 52
    });

    $scope.downloads.push ({
      pos:       3,
      status:    'RUNNING',
      file:      'Community.S04E05.HDTV.avi',
      loaded:    186646528,
      size:      193986560,
      speed:     347,
      remaining: 3
    });

    $scope.downloads.push ({
      pos:       4,
      status:    'COMPLETE',
      file:      'Community.S04E04.HDTV.avi',
      loaded:    193986560,
      size:      193986560,
      speed:     62,
      remaining: 0
    });

  }]);