package handlers

import (
	"auth/internal/middleware"
	"gorm.io/gorm"
	"net/http"
)

func InitializeRouter(db *gorm.DB) *http.ServeMux {
	router := http.NewServeMux()

	pingHandlerWithMiddleware := middleware.Apply(PingHandler(), middleware.WithDB(db))
	router.Handle("/ping", pingHandlerWithMiddleware)

	return router
}
