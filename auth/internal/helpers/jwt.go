package helpers

import (
	"errors"
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"log"
	"os"
	"time"
)

type JwtClaims struct {
	UUID     string `json:"uuid"`
	Username string `json:"username"`
	Role     string `json:"role"`
	jwt.RegisteredClaims
}

var jwtSecret = []byte(os.Getenv("JWT_SECRET"))

func CreateJWT(uuid, username, role string) (string, error) {
	if len(jwtSecret) == 0 {
		return "", errors.New("JWT_SECRET environment variable is not set")
	}

	claims := JwtClaims{
		UUID:     uuid,
		Username: username,
		Role:     role,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString(jwtSecret)
	if err != nil {
		log.Printf("Error generating JWT: %v", err)
		return "", err
	}
	return tokenString, nil
}

func VerifyJWT(tokenStr string) (*JwtClaims, error) {
	if len(jwtSecret) == 0 {
		return nil, errors.New("JWT_SECRET environment variable is not set")
	}

	token, err := jwt.ParseWithClaims(tokenStr, &JwtClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return jwtSecret, nil
	})
	if err != nil {
		return nil, err
	}

	claims, ok := token.Claims.(*JwtClaims)
	if !ok || !token.Valid {
		return nil, errors.New("invalid token")
	}
	return claims, nil
}
