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
	DB *gorm.DB
}

func New() *App {
	database := db.Init()
	if err := database.AutoMigrate(&models.User{}); err != nil {
		log.Fatal(err)
	}
	return &App{DB: database}
}

func (a *App) InitializeRoutes() {
	http.HandleFunc("/ping", handlers.PingHandler())
}

func (a *App) Run(addr string) {
	log.Println("listening on", addr)
	if err := http.ListenAndServe(addr, nil); err != nil {
		log.Fatal(err)
	}
}
