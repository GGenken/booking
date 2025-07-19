package app

import (
	"auth/internal/db"
	"auth/internal/handlers"
	"auth/internal/models"
	"gorm.io/gorm"
	"log"
	"net/http"
)

type App struct {
	DB     *gorm.DB
	Router *http.ServeMux
}

func New() *App {
	database := db.Init()
	if err := database.AutoMigrate(&models.User{}); err != nil {
		log.Fatal(err)
	}
	router := handlers.InitializeRouter(database)
	return &App{DB: database, Router: router}
}

func (a *App) Run(addr string) {
	log.Println("listening on", addr)
	if err := http.ListenAndServe(addr, a.Router); err != nil {
		log.Fatal(err)
	}
}
