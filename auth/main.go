package main

import (
	"auth/internal/app"
)

func main() {
	a := app.New()
	a.Run(":8080")
}
