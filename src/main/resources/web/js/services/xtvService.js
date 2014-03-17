angular.module ('xtv.services').factory('xtvService', [ '$rootScope', '$q', '$timeout', 'vertxEventBusService', function($rootScope, $q, $timeout, vertxEventBusService) {

  var connected = false;

  $rootScope.$on('vertx-eventbus.system.connected', function() {
    console.log('eventbus connected');
    connected = true;
  });

  return {
        send: function (address, data) {
          if (connected) {
            return vertxEventBusService.send(address, data, true);
          } else {
            var deferred = $q.defer ();
            $timeout(function() {
              deferred.resolve(vertxEventBusService.send(address, data, true));
            }, 2000);
            return deferred.promise;
          }
        }
    }
}]);