package helpers

import (
	"errors"
	"github.com/golang-jwt/jwt"
	"log"
	"os"
	"time"
)

var jwtSecret = []byte(os.Getenv("JWT_SECRET"))

func CreateJWT(uuid string, username, role string) (string, error) {
	if len(jwtSecret) == 0 {
		return "", errors.New("JWT_SECRET environment variable is not set")
	}

	claims := jwt.MapClaims{
		"uuid":     uuid,
		"username": username,
		"role":     role,
		"exp":      time.Now().Add(time.Hour * 24).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString(jwtSecret)

	if err != nil {
		log.Printf("Error generating JWT: %v", err)
		return "", err
	}
	return tokenString, nil
}
