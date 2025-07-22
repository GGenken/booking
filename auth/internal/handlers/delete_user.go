package handlers

import (
	"auth/internal/helpers"
	"auth/internal/models"
	"gorm.io/gorm"
	"net/http"
	"strings"
)

func DeleteUserHandler(db *gorm.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		parts := strings.Split(strings.Trim(r.URL.Path, "/"), "/")
		if len(parts) != 2 || parts[0] != "users" {
			helpers.SendErrorResponse(w, http.StatusBadRequest, "invalid path, need /users/{uuid}")
			return
		}
		uuid := parts[1]

		if result := db.Where("uuid = ?", uuid).Delete(&models.User{}); result.RowsAffected == 0 {
			http.Error(w, "user not found", http.StatusNotFound)
			return
		} else if result.Error != nil {
			http.Error(w, result.Error.Error(), http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusNoContent)
	}
}
