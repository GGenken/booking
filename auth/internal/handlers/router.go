package handlers

import (
	"auth/internal/middleware"
	"gorm.io/gorm"
	"net/http"
)

func InitializeRouter(db *gorm.DB) *http.ServeMux {
	router := http.NewServeMux()

	router.Handle(
		"/ping",
		middleware.Apply(
			PingHandler(),
			middleware.WithDB(db),
		),
	)
	router.Handle(
		"/register",
		middleware.Apply(
			RegisterHandler(),
			middleware.WithDB(db),
			middleware.JSONContentType,
		),
	)
	router.Handle(
		"/login",
		middleware.Apply(
			LoginHandler(),
			middleware.WithDB(db),
			middleware.JSONContentType,
		),
	)

	return router
}
