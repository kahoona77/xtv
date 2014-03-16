'use strict';

/* Controllers */

angular.module('xtv.controllers', []).
  controller('MyCtrl1', ['$scope', 'vertxEventBusService', function($scope, vertxEventBusService) {

        vertxEventBusService.on('test.new', function(message){
            console.log('got test new: ', message);
        });

        $scope.$on('vertx-eventbus.system.connected', function() {
            console.log('connected');

            vertxEventBusService.send('testdata.servers', {data: 123}, true).then(function(reply){
                $scope.servers = reply.data;
            }).catch(function(){
                console.warn('No message');
            });

            vertxEventBusService.send('test.msg', {data: 123}, true).then(function(reply){
                console.log('A reply received: ', reply);
            }).catch(function(){
                console.warn('No message');
            });



        });



  }])
  .controller('MyCtrl2', [function() {

  }]);