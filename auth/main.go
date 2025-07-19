package main

import (
	"auth/internal/app"
)

func main() {
	a := app.New()
	a.InitializeRoutes()
	a.Run(":8080")
}
