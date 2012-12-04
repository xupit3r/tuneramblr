# tuneramblr

The aim of tuneramblr is to provide recommendations for music that are based on how you listen to music (and not just what you listen to).

## Deploying the code
Really the only thing that needs to be setup to deploy this code is the DB.  The DB (Mongo) connection setting can be configured within tuneramblr.properties.  For the exact properties to set, see dbdef.clj.

Tuneramblr uses Leiningen for project management, so, as soon as you set up the DB, you can deploy the application by entering the command `lein run` from within the tunerambr directory.

## License

Copyright (C) 2012 Joe D'Alessandro

Distributed under the Eclipse Public License, the same as Clojure.

