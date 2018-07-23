<!DOCTYPE html>
<html ng-app="app">
<head>
    <meta charset="utf-8" />
    <title>Award Reward</title>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" />
    <link href="css/app-content/app.css" rel="stylesheet" />
    
    <link href="https://cdn.bootcss.com/zTree.v3/3.5.29/css/zTreeStyle/zTreeStyle.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/zTree.v3/3.5.29/js/jquery.ztree.all.min.js"></script>
    
</head>
<body>
    <div class="jumbotron">
        <div class="container">
            <div class="col-sm-8 col-sm-offset-2">
                <div ng-class="{ 'alert': flash, 'alert-success': flash.type === 'success', 'alert-danger': flash.type === 'error' }" ng-if="flash" ng-bind="flash.message"></div>
                <div ng-view></div>
            </div>
        </div>
    </div>

    <script src="//code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="//code.angularjs.org/1.6.0/angular.min.js"></script>
    <script src="//code.angularjs.org/1.6.0/angular-route.min.js"></script>
    <script src="//code.angularjs.org/1.6.0/angular-cookies.min.js"></script>

    <script src="js/app.js"></script>
    <script src="js/app-services/authentication.service.js"></script>
    <script src="js/app-services/flash.service.js"></script>

    <!-- Real user service that uses an api -->
    <script src="js/app-services/user.service.js"></script>

    <!-- Fake user service for demo that uses local storage -->
    <!-- <script src="js/app-services/user.service.local-storage.js"></script>  -->

    <script src="js/home/home.controller.js"></script>
    <script src="js/login/login.controller.js"></script>
    <script src="js/register/register.controller.js"></script>
</body>
</html>