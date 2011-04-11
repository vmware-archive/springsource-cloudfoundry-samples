(function() {
  ['provider', 'connect'].forEach(function(path) {
    var foo, module, name, _results;
    module = require('./' + path);
    _results = [];
    for (name in module) {
      foo = module[name];
      _results.push(exports[name] = foo);
    }
    return _results;
  });
}).call(this);
