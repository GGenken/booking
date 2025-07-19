package middleware

import "net/http"

type Middleware func(http.Handler) http.Handler

func Apply(handler http.Handler, middlewares ...Middleware) http.Handler {
	for m := len(middlewares) - 1; m >= 0; m-- {
		handler = middlewares[m](handler)
	}
	return handler
}
