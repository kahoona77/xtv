'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('ShowsCtrl', ['$scope', 'msg', '$http', '$location', function($scope, msg, $http, $location) {

    $scope.loadShows = function () {
      $http.get('shows/load').success(function(response){
        if (response.status == 'ok') {
          $scope.shows = response.results;

          //reselect server
          angular.forEach ($scope.shows, function (show) {
            if ($scope.selectedShow) {
              if (show._id == $scope.selectedShow._id) {
                $scope.selectedShow = show;
              }
            }
          });
        } else {
          msg.error (response.message);
        }
      });
    };
    $scope.loadShows();

    $scope.showAddShowDialog = function () {
      $('#addShowDialog').modal('show');
    };

    $scope.selectShow = function (show) {
      $scope.selectedShow = show;
    };

    $scope.showDeleteShowConfirm = function (show) {
      $scope.showToDelete = show;
      $('#deleteShowConfirmDialog').modal ('show');
    };

    $scope.deleteShow = function () {
      $http.post ('shows/delete', {data: $scope.showToDelete}).success (function (response) {
        if (response.status = 'ok') {
          $('#deleteShowConfirmDialog').modal('hide');
          $scope.showToDelete = undefined;
          $scope.loadShows();
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.searchEpisode = function (episode) {
      var pad = "00"
      var season = "" + episode.season;
      season =  pad.substring(0, pad.length - season) + season;

      var number = "" + episode.number;
      number =  pad.substring(0, pad.length - number) + number;

      var query = episode.series + " S" +  season + "E" + number;
      $location.path ('/search/' + query);
    };

    //search
    $scope.searchShow = function () {

      $http.get('shows/search', {params : {query: $scope.query}}).success(function(response){
        if (response.status == 'ok') {
          $scope.searchResults = response.results;
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.addShow = function (show) {
      $http.post ('shows/add', {data: show}).success (function (response) {
        if (response.status = 'ok') {
          $('#addShowDialog').modal('hide');
          $scope.query = undefined;
          $scope.loadShows();
        } else {
          msg.error (response.message);
        }
      });
    };



  }]);