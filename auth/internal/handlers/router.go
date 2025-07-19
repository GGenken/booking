package handlers

import (
	"gorm.io/gorm"
	"net/http"
)

func InitializeRouter(db *gorm.DB) *http.ServeMux {
	router := http.NewServeMux()

	router.Handle("/ping", PingHandler())

	return router
}
