package handlers

import (
	"auth/internal/helpers"
	"auth/internal/middleware"
	"auth/internal/models"
	"errors"
	"gorm.io/gorm"
	"net/http"
)

func JWTVerificationHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		token := r.Header.Get("X-Auth-Token")
		if token == "" {
			helpers.SendErrorResponse(w, http.StatusUnauthorized, "Token is required")
			return
		}

		claims, err := helpers.VerifyJWT(token)
		if err != nil {
			helpers.SendErrorResponse(w, http.StatusUnauthorized, "Invalid token")
			return
		}

		db := middleware.GetDB(r)
		var user models.User
		if err := db.Where("uuid = ?", claims.UUID).First(&user).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				helpers.SendErrorResponse(w, http.StatusUnauthorized, "User not found")
			} else {
				helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
			}
			return
		}

		w.WriteHeader(http.StatusOK)
	}
}
