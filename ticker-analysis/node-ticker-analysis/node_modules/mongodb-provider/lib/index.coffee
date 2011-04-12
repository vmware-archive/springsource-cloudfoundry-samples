['provider', 'connect'].forEach (path)->
  module = require './' + path
  for name, foo of module
    exports[name] = foo
