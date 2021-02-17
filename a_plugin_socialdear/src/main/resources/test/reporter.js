
function reporter() {
    this.jasmineStarted = function (options) { };
    this.specStarted = function (result) { };
    this.specDone = async function (result) {
       console.log('testoutput:'+JSON.stringify(result));
    };

    this.suiteStarted = function (result) { };
    this.suiteDone = function (result) { }
    this.jasmineDone = async function (result) { }
}

(function (window) {
    var env = jasmine.getEnv();
    env.addReporter(new reporter());
})(window);