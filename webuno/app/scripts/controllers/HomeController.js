'use strict';

angular.module('unoApp')
    .controller('HomeController', ['$scope', '$timeout', '$http', '$state', 'Games', 'Game', function ($scope, $timeout, $http, $state, Games, Game) {
        $scope.games = Games.data.games;
        var timeoutListGames;

        $scope.requestListGames = function () {
            timeoutListGames = $timeout(function () {
                Game.getAllGames()
                    .then(function (data) {
                        $scope.games = data.data.games;
                        $scope.requestListGames();
                    }, function (error) {
                        console.error(error);
                        $scope.requestListGames();
                    });
            }, 5000);
        };

        $scope.requestListGames();

        $scope.joinGame = function (gameName) {
            Game.joinGame(gameName)
                .then(function (response) {
                    switch (response.status) {
                        case 200 :
                            if (response.data.status) {
                                $state.go('app.room', {name: gameName});
                            } else {
                                console.error('MyError 1 : ', response);
                            }
                            break;
                        default :
                            console.error('MyError 2 : ', response);
                    }
                }, function (error) {
                    console.error(error);
                });
        };

        $scope.$on('$destroy', function(){
            $timeout.cancel(timeoutListGames);
        });
    }]);
