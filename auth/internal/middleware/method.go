package middleware

import (
	"auth/internal/helpers"
	"net/http"
)

func Method(method string) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			if r.Method != method {
				w.Header().Set("Allow", method)
				helpers.SendErrorResponse(w, http.StatusMethodNotAllowed, "Method Not Allowed")
				return
			}
			next.ServeHTTP(w, r)
		})
	}
}
