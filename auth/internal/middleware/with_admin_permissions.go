package middleware

import (
	"auth/internal/helpers"
	"auth/internal/models"
	"errors"
	"gorm.io/gorm"
	"net/http"
)

func WithAdminPermissions() func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			token := r.Header.Get("X-Auth-Token")
			if token == "" {
				helpers.SendErrorResponse(w, http.StatusUnauthorized, "X-Auth-Token header is required")
				return
			}

			claims, err := helpers.VerifyJWT(token)
			if err != nil {
				helpers.SendErrorResponse(w, http.StatusForbidden, "Invalid or expired token")
				return
			}

			if claims.Role != "admin" {
				helpers.SendErrorResponse(w, http.StatusForbidden, "Insufficient privileges")
				return
			}

			db := GetDB(r)
			var user models.User
			if err := db.Where("uuid = ?", claims.UUID).First(&user).Error; err != nil {
				if errors.Is(err, gorm.ErrRecordNotFound) {
					helpers.SendErrorResponse(w, http.StatusForbidden, "Insufficient privileges")
					return
				}
				helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
				return
			}
			if user.Role != "admin" {
				helpers.SendErrorResponse(w, http.StatusForbidden, "Insufficient privileges")
				return
			}

			next.ServeHTTP(w, r)
		})
	}
}
