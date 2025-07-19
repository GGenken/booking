package handlers

import (
	"auth/internal/middleware"
	"net/http"
)

func PingHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		middleware.GetDB(r)
		w.WriteHeader(http.StatusOK)
	}
}
