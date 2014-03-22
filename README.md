# Docmogrify

A lightweight API server to convert PDF documents for prince xml.

# Usage

POST JSON to /convert with the following options:

    {
      "output-file":  "A Test PDF.pdf", // The name of the output file in API response
      "data": "http://www.google.com",  // URL of document to convert, or raw HTML string
        // Prince Command Line Options
        "options": {
        "--disallow-annotate": true
      }
    }

Response will be a PDF download with the content-disposition set to attachment with filename that was
chosen in the "output-file" JSON member.

Authentication is handled by `ring-token-authentication` found here: [here](https://github.com/jstewart/ring-token-authentication).
The environment variable `AUTH_TOKEN` must be set when running in production mode.


## Prerequisites


You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a development web server for the application, run:

    lein ring server

To run in production, the application is bundled with jetty, a high performance HTTP server.
you'll want to build a jar or war with leiningen, then set `PORT` and/or `AUTH_TOKEN` before startup.

## License

Copyright 2014 Jason Stewart

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
