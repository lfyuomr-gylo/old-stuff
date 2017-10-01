// open given in command line argument file and perform it as lua source
fs = require('fs');
var argv = process.argv;
if (argv[2]) {
  fs.readFile(argv[2], 'utf8', function(err, data) {
    if (err) {
      return console.log(err);
    }

    var start = Date.now();
    runLuaCode(data);
    var run_time = Date.now() - start;
    console.log('\'pure time\': ' + run_time / 1000 + ',');
  })
}
else {
//  console.log('argument was not pass');
}
